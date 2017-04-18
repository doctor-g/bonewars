package edu.bsu.bonewars.core.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.Test;

import edu.bsu.bonewars.core.model.Fossil.Quality;
import edu.bsu.bonewars.core.model.Fossil.Type;

public class FossilTest {

	private Quality quality = Quality.MEDIUM;
	private Type type = Type.B;

	@Test
	public void testFossilHasExpectedType() {
		Fossil aFossil = Fossil.createWithQuality(quality).andWithType(type);
		assertEquals(Type.B, aFossil.type());
	}

	@Test
	public void testFossilHasExpectedQuality() {
		Fossil aFossil = Fossil.createWithQuality(quality).andWithType(type);
		assertEquals(Quality.MEDIUM, aFossil.quality());
	}

	@Test
	public void testThatAFossilIsCreatedUnanlyzed() {
		Fossil aFossil = Fossil.createWithQuality(quality).andWithType(type);
		assertFalse(aFossil.isAnalyzed().get());
	}

	@Test
	public void testFossilCanBeAnalyzed() {
		Fossil aFossil = Fossil.createWithQuality(quality).andWithType(type);
		aFossil.analyze();
		assertTrue(aFossil.isAnalyzed().get());
	}

	@Test(expected = IllegalStateException.class)
	public void testErrorIsThrownWhenAnalyzingAnAlreadyAnalyzedFossil() {
		Fossil aFossil = Fossil.createWithQuality(quality).andWithType(type);
		aFossil.analyze();
		aFossil.analyze();
	}

	@Test
	public void testSetPublisher() {
		Fossil aFossil = Fossil.createWithQuality(quality).andWithType(type);
		Player player = mock(Player.class);
		aFossil.setOwner(player);
		assertEquals(player, aFossil.owner());
	}

}
