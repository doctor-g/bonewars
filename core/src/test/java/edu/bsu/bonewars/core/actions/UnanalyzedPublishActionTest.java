package edu.bsu.bonewars.core.actions;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import edu.bsu.bonewars.core.model.Fame;
import edu.bsu.bonewars.core.model.Fossil;
import edu.bsu.bonewars.core.model.Fossil.Quality;
import edu.bsu.bonewars.core.model.Fossil.Type;
import edu.bsu.bonewars.core.model.FossilStack;
import edu.bsu.bonewars.core.model.Game;
import edu.bsu.bonewars.core.model.Player;

public class UnanalyzedPublishActionTest {

	private Game game;
	private Player marsh;
	private Player cope;
	private UnanalyzedPublishedAction action;
	private FossilStack marshStack;
	private FossilStack copeStack;

	@Before
	public void setUpGame() {
		this.game = Game.create();
		this.marsh = game.marsh;
		this.cope = game.cope;
	}

	private void marshPrepareToPublishAnUnanalyzedFossil() {
		Fossil fossilToBePublished = Fossil.createWithQuality(Quality.MEDIUM)
				.andWithType(Type.A);
		fossilToBePublished.setOwner(marsh);
		marshStack = marsh.fossilStack(Type.A);
		marshStack.add(fossilToBePublished);
		action = UnanalyzedPublishedAction.create(marshStack);
		marsh.bigWorker().select();

	}

	private void copePrepareToPublishUnanalyzedFossil() {
		Fossil secondFossilToBePublished = Fossil
				.createWithQuality(Quality.LOW).andWithType(Type.A);
		secondFossilToBePublished.setOwner(cope);
		copeStack = cope.fossilStack(Type.A);
		copeStack.add(secondFossilToBePublished);
		action = UnanalyzedPublishedAction.create(copeStack);
		cope.bigWorker().select();

	}

	@Test
	public void testMarshGainsFamePlusBonusForPublishingUnanalyzedFossil() {
		marshPrepareToPublishAnUnanalyzedFossil();
		action.doAction();
		Fame fameOfCurrentPlayerAfterAction = marsh.fame().get();
		Fame expected = Quality.MEDIUM.fame().add(Game.FIRST_PUBLISH_BONUS);
		assertEquals(expected, fameOfCurrentPlayerAfterAction);
	}

	@Test
	public void testCopeLosesFameForPublishingALowerQualityFossil() {
		marshPrepareToPublishAnUnanalyzedFossil();
		action.doAction();
		copePrepareToPublishUnanalyzedFossil();
		cope.addFame(Fame.valueOf(5));
		Fame fameOfCopeBeforeAction = cope.fame().get();
		action.doAction();
		Fame fameOfCopeAfterAction = cope.fame().get();
		assertEquals(
				Fame.valueOf(fameOfCopeBeforeAction.toInt()
						- Game.FAME_LOWER_QUALITY_VALUE.toInt()),
				fameOfCopeAfterAction);
	}

	@Test
	public void testPlayerFameCannotBelowZero() {
		marshPrepareToPublishAnUnanalyzedFossil();
		action.doAction();
		copePrepareToPublishUnanalyzedFossil();
		action.doAction();
		Fame expected = Fame.valueOf(0);
		Fame fameOfCopeAfterAction = cope.fame().get();
		assertEquals(expected, fameOfCopeAfterAction);
	}
}
