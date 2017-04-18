package edu.bsu.bonewars.core.actions;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import edu.bsu.bonewars.core.model.Fossil;
import edu.bsu.bonewars.core.model.Fossil.Quality;
import edu.bsu.bonewars.core.model.Fossil.Type;
import edu.bsu.bonewars.core.model.FossilStack;
import edu.bsu.bonewars.core.model.Game;
import edu.bsu.bonewars.core.model.Player;

public class AnalyzeActionTest {

	private Game game;
	private AnalyzeAction action;
	private Player currentPlayer;
	private Fossil aFossil;
	private FossilStack fossilStack;

	@Before
	public void setup() {
		game = Game.create();
		aFossil = Fossil.createWithQuality(Quality.HIGH).andWithType(Type.A);
		fossilStack = FossilStack.createOfType(Type.A);
		fossilStack.add(aFossil);
		currentPlayer = game.currentPlayer().get();
	}

	@Test
	public void testAFossilHasBeenAnalyzed() {
		Game.create();
		currentPlayer = Game.currentGame().currentPlayer().get();
		action = AnalyzeAction.create(fossilStack);
		currentPlayer.addFossil(aFossil);
		currentPlayer.bigWorker().select();
		action.doAction();
		assertTrue(aFossil.isAnalyzed().get());
	}

	@Test
	public void testActionIsNotAvaliableWhenInitialzed() {
		action = AnalyzeAction.create(fossilStack);
		currentPlayer.addFossil(aFossil);
		assertFalse(action.isAvailable());
	}

	@Test
	public void testActionBecomesAvailableWhenLittleWorkerIsMadeCurrentWorkerAndThereIsAFossilReadyForAnalyzing() {
		action = AnalyzeAction.create(fossilStack);
		currentPlayer.addFossil(aFossil);
		game.currentPlayer().get().littleWorkerCollection().get(0).select();
		assertTrue(action.isAvailable());
	}

	@Test
	public void testActionBecomesAvailableWhenBigWorkerIsMadeCurrentWorkerAndThereIsAFossilReadyForAnalyzing() {
		action = AnalyzeAction.create(fossilStack);
		currentPlayer.addFossil(aFossil);
		game.currentPlayer().get().bigWorker().select();
		assertTrue(action.isAvailable());
	}
}