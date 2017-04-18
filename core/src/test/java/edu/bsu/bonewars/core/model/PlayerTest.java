package edu.bsu.bonewars.core.model;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import edu.bsu.bonewars.core.model.Fossil.Type;

public class PlayerTest {

	private Player player;

	@Before
	public void setUp() {
		player = Player.createCope();
	}

	@Test
	public void testPlayerIsCreatedHavingThreeWorkers() {
		assertEquals(Player.NUMBER_OF_STARTING_LITTLE_WORKERS, player
				.littleWorkerCollection().size());
	}

	@Test
	public void testFossilStackIsReturnedForAGivenFossilType() {
		FossilStack stack = player.fossilStack(Type.A);
		assertEquals(Type.A, stack.type());
	}

}
