package edu.bsu.bonewars.core.model;

import static com.google.common.base.Preconditions.checkState;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

import react.Slot;

public class UpkeepTest {

	private Game game;
	private Upkeep upkeep;

	@Before
	public void setUp() {
		game = Game.create();
	}

	@Test
	public void testDefaultMarshSelectionIsStartingNumberOfWorkers() {
		whenMarshHasTwoWorkers();
		assertEquals(2, upkeep.marsh.retainedWorkers());
	}

	private void whenMarshHasTwoWorkers() {
		upkeep = Upkeep.create();
		checkState(2 == numberOfMarshWorkers());
	}

	private int numberOfMarshWorkers() {
		return game.marsh.littleWorkerCollection().size();
	}

	@Test
	public void testDefaultSelection_threeWorkers() {
		whenMarshHasThreeWorkers();
		assertEquals(3, upkeep.marsh.retainedWorkers());
	}

	private void whenMarshHasThreeWorkers() {
		Game.currentGame().marsh.addLittleWorker();
		checkState(numberOfMarshWorkers() == 3);
		upkeep = Upkeep.create();
	}

	@Test
	public void testDefaultMarshSelectionSalary_initial() {
		Upkeep upkeep = Upkeep.create();
		assertEquals(2 * Player.NUMBER_OF_STARTING_LITTLE_WORKERS,
				upkeep.marsh.salaryExpense());
	}

	@Test
	public void testMarshMaxIsNumberOfWorkers_enoughFunds() {
		Upkeep upkeep = Upkeep.create();
		assertEquals(Player.NUMBER_OF_STARTING_LITTLE_WORKERS,
				upkeep.marsh.max());
	}

	@Test
	public void testSignalOnSelectionChange() {
		whenMarshHasTwoWorkers();
		@SuppressWarnings("unchecked")
		Slot<Integer> slot = mock(Slot.class);
		upkeep.marsh.selection().connect(slot);
		upkeep.marsh.select(0);
		verify(slot).onEmit(0);
	}

	@Test
	public void testFireWorkers_none() {
		whenMarshHasTwoWorkers();
		upkeep.marsh.fireWorkersBasedOnSelection();
		int workerCountAfter = numberOfMarshWorkers();
		assertEquals(2, workerCountAfter);
	}

	@Test
	public void testFireWorkers_one() {
		whenMarshHasTwoWorkers();
		upkeep.marsh.select(1);
		upkeep.marsh.fireWorkersBasedOnSelection();
		int workerCountAfter = numberOfMarshWorkers();
		assertEquals(1, workerCountAfter);
	}

	@Test
	public void testMax_canAffordOnlyOne() {
		whenMarshHasTwoWorkers();
		andMarshHasOnlyEnoughToAffordOne();
		assertEquals(1, upkeep.marsh.max());
	}

	private void andMarshHasOnlyEnoughToAffordOne() {
		game.marsh.setFunds(Funds.valueOf(Upkeep.UPKEEP_SALARY_PER_WORKER));
	}
}
