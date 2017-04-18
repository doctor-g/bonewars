package edu.bsu.bonewars.core.actions;

import static com.google.common.base.Preconditions.checkState;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import edu.bsu.bonewars.core.model.Action;
import edu.bsu.bonewars.core.model.Fame;
import edu.bsu.bonewars.core.model.Fossil;
import edu.bsu.bonewars.core.model.Fossil.Quality;
import edu.bsu.bonewars.core.model.Fossil.Type;
import edu.bsu.bonewars.core.model.FossilStack;
import edu.bsu.bonewars.core.model.Game;
import edu.bsu.bonewars.core.model.Player;

public class AnalyzedPublishActionTest {

	private static final Quality MARSH_PUBLISH_QUALITY = Quality.MEDIUM;
	private static final Quality TRUMP_QUALITY = Quality.HIGH;
	private static final Type PUBLISHED_FOSSIL_TYPE = Type.A;

	private Game game;
	private Player marsh;
	private Player cope;
	private Fossil fossil;
	private FossilStack fossilStack;

	@Before
	public void setUp() {
		this.game = Game.create();
		this.marsh = game.marsh;
		this.cope = game.cope;
	}

	public Action marshPublishedAFossil() {
		checkState(game.currentPlayer().get().equals(marsh));
		fossil = Fossil.createWithQuality(MARSH_PUBLISH_QUALITY).andWithType(
				PUBLISHED_FOSSIL_TYPE);
		fossil.setOwner(marsh);
		fossil.analyze();
		FossilStack fossilStack = marsh.fossilStack(PUBLISHED_FOSSIL_TYPE);
		fossilStack.add(fossil);
		AnalyzedPublishAction action = AnalyzedPublishAction.create(fossilStack);
		marsh.bigWorker().select();
		action.doAction();
		return action;
	}

	@Test
	public void testMarshGainsFameForPublishing_qualityFamePlusFirstFossilBonus() {
		marshPublishedAFossil();
		Fame fameOfCurrentPlayerAfterAction = marsh.fame().get();
		Fame expected = MARSH_PUBLISH_QUALITY.fame().add(
				Game.FIRST_PUBLISH_BONUS);
		assertEquals(expected, fameOfCurrentPlayerAfterAction);
	}

	@Test
	public void testCopeGainsFameForPublishing_marshAlreadyPublished_noBonus() {
		marshPublishedAFossil();
		Fame copeFameBeforePublish = cope.fame().get();
		copePublishes(MARSH_PUBLISH_QUALITY);
		Fame expected = copeFameBeforePublish.add(MARSH_PUBLISH_QUALITY.fame());
		assertEquals(expected, cope.fame().get());
	}
	
	private void copePublishes(Quality fossilQuality) {
		Type trumpType = Type.A;
		Fossil trumpFossil = Fossil.createWithQuality(fossilQuality)
				.andWithType(trumpType);
		trumpFossil.setOwner(cope);
		trumpFossil.analyze();
		FossilStack fossilStack = cope.fossilStack(trumpType);
		fossilStack.add(trumpFossil);
		AnalyzedPublishAction action = AnalyzedPublishAction.create(fossilStack);
		cope.bigWorker().select();
		action.doAction();
	}

	@Test
	public void testMarshHasZeroFossilsAfterPublishingHisOnlyOne() {
		marshPublishedAFossil();
		assertTrue(marsh.fossilStack(PUBLISHED_FOSSIL_TYPE).isEmpty());
	}

	@Test
	public void testAFossilIsMadePublisherOfAFossil() {
		marshPublishedAFossil();
		assertEquals(marsh, fossil.owner());
	}

	@Test
	public void testAFossilIsAddedToPubishedFossilCollection() {
		marshPublishedAFossil();
		assertEquals(
				game.publishedFossilsArea()
						.getPublishedFossilsOfType(fossil.type())
						.getAllFossils().size(), 1);
	}

	@Test
	public void testThatActionIsNotAvailableAfterDoingTheAction() {
		Action action = marshPublishedAFossil();
		assertFalse(action.available().get());
	}

	@Test
	public void testActionIsNotAvailableForAFossilOfLowerQualityIfAHigherQualityFossilHasAlreadyBeenPublished() {
		marshPublishedAFossil();
		Fossil secondFossilToBePublished = Fossil
				.createWithQuality(Quality.LOW).andWithType(
						PUBLISHED_FOSSIL_TYPE);
		secondFossilToBePublished.setOwner(marsh);
		secondFossilToBePublished.analyze();
		fossilStack = marsh.fossilStack(PUBLISHED_FOSSIL_TYPE);
		fossilStack.add(secondFossilToBePublished);
		AnalyzedPublishAction secondAction = AnalyzedPublishAction
				.create(fossilStack);
		assertFalse(secondAction.available().get());
	}

	@Test
	public void testCopeGainsFame_trump() {
		marshPublishedAFossil();
		Fame copeFameBeforePublish = cope.fame().get();
		copePublishesTrumpFossil();
		Fame expected = copeFameBeforePublish.add(TRUMP_QUALITY.fame().add(
				Game.FAME_TRUMP_STEAL_VALUE));
		assertEquals(expected, cope.fame().get());
	}

	private void copePublishesTrumpFossil() {
		Type trumpType = Type.A;
		Fossil trumpFossil = Fossil.createWithQuality(TRUMP_QUALITY)
				.andWithType(trumpType);
		trumpFossil.setOwner(cope);
		trumpFossil.analyze();
		FossilStack fossilStack = cope.fossilStack(trumpType);
		fossilStack.add(trumpFossil);
		AnalyzedPublishAction action = AnalyzedPublishAction.create(fossilStack);
		cope.bigWorker().select();
		action.doAction();
	}

	@Test
	public void testMarshLosesFame_trump() {
		marshPublishedAFossil();
		Fame marshFameBeforeCopeTurn = marsh.fame().get();
		copePublishesTrumpFossil();
		Fame expected = marshFameBeforeCopeTurn
				.subtract(Game.FAME_TRUMP_STEAL_VALUE);
		assertEquals(expected, marsh.fame().get());
	}
}