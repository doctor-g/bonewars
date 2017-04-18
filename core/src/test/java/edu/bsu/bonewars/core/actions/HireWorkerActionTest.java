package edu.bsu.bonewars.core.actions;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import edu.bsu.bonewars.core.model.Game;
import edu.bsu.bonewars.core.model.Player;
import edu.bsu.bonewars.core.model.Worker;

public class HireWorkerActionTest {
	
	private Worker aWorker;
	private HireWorkerAction action;
	private Player player;
	
	@Before
	public void setup() {
		Game.create();
		player = Game.currentGame().currentPlayer().get();
		checkNotNull(player);
		action = Game.currentGame().actionRegistry().getHireWorkerAction(player);
		checkNotNull(player.bigWorker());
		aWorker = player.bigWorker();
	}
	
	@Test
	public void testActionBecomesAvailableWhenBigWorkerIsSelected() {
		aWorker.select();
		assertTrue(action.available().get());
	}
	
	@Test
	public void testPlayerGainsWorkerAfterHireWorkerAction() {
		setup();
		aWorker.select();
		action.doAction();
		assertEquals(3, player.littleWorkerCollection().size());
	}
	
	@Test
	public void testHireWorkerActionIsUnavailableAfterDoingAction() {
		aWorker.select();
		action.doAction();
		assertFalse(action.available().get());
	}
	
	@Test
	public void testWorkerCannotBeSelectedAfterAction() {
		aWorker.select();
		action.doAction();
		assertFalse(aWorker.isSelected());
	}
	
	@Test
	public void testHireWorkerActionIsNotAvailableWhenPlayerAlreadyHasMaxNumberOfWorkers(){
		while(player.littleWorkerCollection().size() < Player.MAX_NUMBER_OF_LITTLE_WORKERS){
			player.addLittleWorker();
		}
		aWorker.select();
		assertFalse(action.available().get());
	}

}
