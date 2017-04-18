package edu.bsu.bonewars.core.model;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import edu.bsu.bonewars.core.actions.AcquireSiteAction;
import edu.bsu.bonewars.core.actions.AnalyzeAction;
import edu.bsu.bonewars.core.actions.AnalyzedPublishAction;
import edu.bsu.bonewars.core.actions.ExcavateAction;
import edu.bsu.bonewars.core.actions.HireWorkerAction;
import edu.bsu.bonewars.core.model.Fossil.Type;

public class ActionRegistryTest {

	private ActionRegistry registry;

	@Before
	public void setUp() {
		Game.create();
		registry = ActionRegistry.create();
	}

	@Test
	public void testRegistryReturnsExcavateActionForGivenSite() {
		Site aSite = Site.createWithFossils(Lists
				.newArrayList(mock(Fossil.class)));
		ExcavateAction excavateAction = ExcavateAction.createWithSite(aSite);
		registry.registerExcavateAction(excavateAction);
		assertEquals(excavateAction, registry.getExcavateActionForSite(aSite));
	}

	@Test(expected = IllegalStateException.class)
	public void textExceptionIsThrownWhenAskingRegistryForExcavationActionWithASiteThatHasNotBeenRegistered() {
		Site aSite = Site.createWithFossils(Lists
				.newArrayList(mock(Fossil.class)));
		registry.getExcavateActionForSite(aSite);
	}

	@Test
	public void testRegistryReturnsAcquireSiteActionForGivenSite() {
		Site aSite = Site.createWithFossils(Lists
				.newArrayList(mock(Fossil.class)));
		ActionRegistry registry = ActionRegistry.create();
		AcquireSiteAction acquireSite = AcquireSiteAction.createWithSite(aSite);
		registry.registerAcquireSiteAction(acquireSite);
		assertEquals(acquireSite, registry.getAcquireSiteActionForSite(aSite));
	}

	@Test
	public void testRegistryReturnsAcquireSiteActionForGivenSite_moreThanOneRegistered() {
		Site aSite = Site.createWithFossils(Lists
				.newArrayList(mock(Fossil.class)));
		AcquireSiteAction acquireSite = AcquireSiteAction.createWithSite(aSite);
		registry.registerAcquireSiteAction(acquireSite);
		Site aSite2 = Site.createWithFossils(Lists
				.newArrayList(mock(Fossil.class)));
		AcquireSiteAction acquireSite2 = AcquireSiteAction
				.createWithSite(aSite2);
		registry.registerAcquireSiteAction(acquireSite2);
		assertEquals(acquireSite, registry.getAcquireSiteActionForSite(aSite));
	}
	
	@Test
	public void testRegistryReturnsAcquireSiteActionForGivenSite_moreThanOneRegistered_alt() {
		Site aSite = Site.createWithFossils(Lists
				.newArrayList(mock(Fossil.class)));
		AcquireSiteAction acquireSite = AcquireSiteAction.createWithSite(aSite);
		registry.registerAcquireSiteAction(acquireSite);
		Site aSite2 = Site.createWithFossils(Lists
				.newArrayList(mock(Fossil.class)));
		AcquireSiteAction acquireSite2 = AcquireSiteAction
				.createWithSite(aSite2);
		registry.registerAcquireSiteAction(acquireSite2);
		assertEquals(acquireSite2, registry.getAcquireSiteActionForSite(aSite2));
	}

	@Test(expected = IllegalStateException.class)
	public void textExceptionIsThrownWhenAskingRegistryForAcquireSiteActionWithASiteThatHasNotBeenRegistered() {
		Site aSite = Site.createWithFossils(Lists
				.newArrayList(mock(Fossil.class)));
		registry.getAcquireSiteActionForSite(aSite);
	}

	@Test
	public void testRegistryReturnsAnalyzeActionForGivenFossil() {
		FossilStack fossilStack = FossilStack.createOfType(Type.B);
		AnalyzeAction analyzeAction = AnalyzeAction.create(fossilStack);
		registry.registerAnalyzeAction(analyzeAction);
		assertEquals(analyzeAction,
				registry.getAnalyzeActionForFossilStack(fossilStack));
	}

	@Test(expected = IllegalStateException.class)
	public void textExceptionIsThrownWhenAskingRegistryForAnalyeActionWithASiteThatHasNotBeenRegistered() {
		FossilStack fossilStack = FossilStack.createOfType(Type.B);
		registry.getAnalyzeActionForFossilStack(fossilStack);
	}

	@Test
	public void testRegistryReturnsAnalyzedPublishActionForGivenFossil() {
		FossilStack aFossilStack = Game.currentGame().currentPlayer().get()
				.fossilStack(Type.B);
		AnalyzedPublishAction analyzedPublishAction = AnalyzedPublishAction
				.create(aFossilStack);
		registry.registerAnalyzedPublishAction(analyzedPublishAction);
		assertEquals(analyzedPublishAction,
				registry.getAnalyzedPublishActionForFossilStack(aFossilStack));
	}

	@Test(expected = IllegalStateException.class)
	public void textExceptionIsThrownWhenAskingRegistryForAnalyedPublishActionWithASiteThatHasNotBeenRegistered() {
		FossilStack aFossilStack = Game.currentGame().currentPlayer().get()
				.fossilStack(Type.B);
		ActionRegistry registry = ActionRegistry.create();
		registry.getAnalyzedPublishActionForFossilStack(aFossilStack);
	}

	@Test
	public void testRegistryReturnsHireWorkerActionForGivenPlayer() {
		Player player = Player.createCope();
		HireWorkerAction hireWorkerAction = HireWorkerAction.create(player);
		registry.registerHireWorkerAction(hireWorkerAction);
		assertEquals(hireWorkerAction, registry.getHireWorkerAction(player));
	}

	@Test(expected = IllegalStateException.class)
	public void textExceptionIsThrownWhenAskingRegistryForHireWorkerActionWithAPlayerThatHasNotBeenRegistered() {
		Player player = Player.createCope();
		registry.getHireWorkerAction(player);
	}

}
