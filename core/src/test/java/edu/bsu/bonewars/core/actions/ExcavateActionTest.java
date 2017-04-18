package edu.bsu.bonewars.core.actions;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import edu.bsu.bonewars.core.model.Fossil;
import edu.bsu.bonewars.core.model.Game;
import edu.bsu.bonewars.core.model.Player;
import edu.bsu.bonewars.core.model.Site;
import edu.bsu.bonewars.core.model.Worker;

public class ExcavateActionTest {

	private Game game;
	private Site site;
	private ExcavateAction action;
	private Player currentPlayer;

	@Before
	public void setup() {
		game = Game.create();
		site = Game.currentGame().sites().get(0);
		action = game.actionRegistry().getExcavateActionForSite(site);
		currentPlayer = game.currentPlayer().get();
		site.setOwner(currentPlayer);
	}

	@Test
	public void testActionBecomesAvailableWhenBigWorkerIsMadeCurrentWorker() {
		selectBigWorker();
		assertTrue(action.available().get());
	}
	
	@Test
	public void testActionBecomesAvailableWhenLittleWorkerIsMadeCurrentWorker() {
		game.currentPlayer().get().littleWorkerCollection().get(0).select();
		assertTrue(action.available().get());
	}

	@Test
	public void testAnExcavatedFossilIsRemovedFromTheSite(){
		selectBigWorker();
		Fossil removedFossil = site.fossils().get(0);
		action.doAction();
		assertFalse(site.fossils().contains(removedFossil));
	}
	
	@Test
	public void testAnExcavatedFossilIsAddedToThePlayersFossilCollection(){
		Player wasCurrentPlayer = currentPlayer;
		selectBigWorker();
		Fossil removedFossil = site.fossils().get(0);
		action.doAction();
		assertTrue(wasCurrentPlayer.fossilStack(removedFossil.type()).contains(removedFossil));
	}

	@Test
	public void testThatActionIsNotAvailableAfterDoingTheAction() {
		selectBigWorker();
		action.doAction();
		assertFalse(action.available().get());
	}

	@Test
	public void testPlayerChangesAfterActionIsComplete() {
		selectBigWorker();
		Player playerBeforeAction = game.currentPlayer().get();
		site.setOwner(playerBeforeAction);
		action.doAction();
		Player playerAfterAction = game.currentPlayer().get();
		assertNotEquals(playerBeforeAction, playerAfterAction);
	}
	
	@Test
	public void testWorkerIsNotSelectedAfterAction() {
		Worker worker = game.currentPlayer().get().bigWorker();
		worker.select();
		action.doAction();
		assertFalse(worker.isSelected());
	}
	
	private void selectBigWorker(){
		game.currentPlayer().get().bigWorker().select();
	}
}
