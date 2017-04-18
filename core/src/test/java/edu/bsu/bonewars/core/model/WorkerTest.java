package edu.bsu.bonewars.core.model;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

import react.Slot;

public class WorkerTest {
	
	private Worker worker;
	
	@Before
	public void setup(){
		Game.create();
		worker = Worker.createLittleWorkerForBoss(Game.currentGame().currentPlayer().get());
	}
	
	@Test
	public void testWorkerIsSelectedAfterCallingSelect(){
		worker.select();
		assertTrue(worker.isSelected());
	}
	
	@Test
	public void testWorkerIsNotSelectedWhenCreated(){
		assertFalse(worker.isSelected());
	}
	
	@Test (expected = IllegalStateException.class)
	public void testDeselectionThrowsExceptionWhenWorkerIsNotAlreadySelected(){
		worker.deselect();
	}
	
	@Test
	public void testWorkedIsNotSelectedAfterSelectingAndDeselecting(){
		worker.select();
		worker.deselect();
		assertFalse(worker.isSelected());
	}
	
	@Test
	public void testWorkerIsCreatedReadyToWork(){
		assertTrue(worker.isReadyForWork().get());
	}
	
	@Test
	public void testWorkerIsNotReadyForWorkAfterBeingUsed(){
		worker.select();
		worker.markAsWorked();
		assertFalse(worker.isReadyForWork().get());
	}
	
	@Test
	public void testWorkerCanMarkedReadyToWork(){
		worker.select();
		worker.markAsWorked();
		worker.makeReadyForWork();
		assertTrue(worker.isReadyForWork().get());
	}
	
	@Test
	public void testWorkerIsUnselectableWhenBossIsNotCurrentPlayer(){
		Player playerWhoIsntCurrentPlayer =  Game.currentGame().getNonCurrentPlayer();
		Worker nonCurrentPlayersWorker = Worker.createLittleWorkerForBoss(playerWhoIsntCurrentPlayer);
		assertFalse(nonCurrentPlayersWorker.isSelectable().get());
	}
	
	@Test (expected = IllegalStateException.class)
	public void testExceptionIsThrownWhenWorkerIsSelectedButUnselectable(){
		Player playerWhoIsntCurrentPlayer =  Game.currentGame().getNonCurrentPlayer();
		Worker nonCurrentPlayersWorker = Worker.createLittleWorkerForBoss(playerWhoIsntCurrentPlayer);
		nonCurrentPlayersWorker.select();
	}
	
	@Test
	public void testIsSelectableChangesToFalseWhenSingleSelectionModelIsDisabled(){
		@SuppressWarnings("unchecked")
		Slot<Boolean> mockSlot = mock(Slot.class);
		worker.isSelectable().connect(mockSlot);
		Game.currentGame().workerSelectionModel().disableSelectionChange();
		verify(mockSlot).onEmit(false);
	}
	
	@Test
	public void testWorkerIsNotSelectableIfWorkerIsNotReadyForWork(){
		worker.markAsWorked();
		assertFalse(worker.isSelectable().get());
	}
}
