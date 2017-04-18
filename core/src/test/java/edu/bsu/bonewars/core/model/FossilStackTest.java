package edu.bsu.bonewars.core.model;

import static com.google.common.base.Preconditions.checkState;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;

import edu.bsu.bonewars.core.model.Fossil.Quality;
import edu.bsu.bonewars.core.model.Fossil.Type;

public class FossilStackTest {

	private static final Fossil.Type FOSSIL_STACK_TYPE = Fossil.Type.D;

	private FossilStack fossilStack;

	@Before
	public void setUp() {
		fossilStack = FossilStack.createOfType(FOSSIL_STACK_TYPE);
	}

	@Test
	public void testThatWhenMultipleFossilsAreAddedToStackThatTheStackWillReturnTheBestOne() {
		Fossil fossil1 = Fossil.createWithQuality(Quality.MEDIUM).andWithType(
				Type.D);
		Fossil fossil2 = Fossil.createWithQuality(Quality.LOW).andWithType(
				Type.D);
		Fossil fossil3 = Fossil.createWithQuality(Quality.LOW).andWithType(
				Type.D);
		Fossil fossil4 = Fossil.createWithQuality(Quality.MEDIUM).andWithType(
				Type.D);
		Fossil fossil5 = Fossil.createWithQuality(Quality.VERY_LOW)
				.andWithType(Type.D);
		fossil2.analyze();
		fossil4.analyze();
		fossil5.analyze();
		fossilStack.add(fossil1);
		fossilStack.add(fossil2);
		fossilStack.add(fossil3);
		fossilStack.add(fossil4);
		fossilStack.add(fossil5);
		assertEquals(fossil4, fossilStack.getBestFossil().get());
	}

	@Test
	public void testThatWhenAFossilIsAddedAndThenRemovedThatTheFossilStackDoesNotHaveABestFossil() {
		Fossil fossil1 = Fossil.createWithQuality(Quality.MEDIUM).andWithType(
				Type.D);
		fossilStack.add(fossil1);
		fossilStack.remove(fossil1);
		assertFalse(fossilStack.hasBestFossil());
	}

	@Test
	public void testThatBestFossilIsUpdatedWhenAFossilIsAnalyzed() {
		Fossil fossil1 = Fossil.createWithQuality(Quality.MEDIUM).andWithType(
				Type.D);
		Fossil fossil2 = Fossil.createWithQuality(Quality.LOW).andWithType(
				Type.D);
		fossilStack.add(fossil1);
		fossilStack.add(fossil2);
		fossil2.analyze();
		assertEquals(fossil2, fossilStack.getBestFossil().get());
	}

	@Test
	public void testAddAnalyzedFossilDoesNotIncreaseUnanalyzedCount() {
		Fossil fossil = Fossil.createWithQuality(Quality.LOW)
				.andWithType(Type.D).analyze();
		fossilStack.add(fossil);
		assertFalse(fossilStack.hasAnUnanalyzedFossil());
	}

	@Test
	public void testNumberOfFossils_none() {
		assertEquals(0, numberOfFossils());
	}

	@Test
	public void testNumberOfFossils_addUnanalyzed() {
		fossilStack.add(unanalyzedFossil());
		assertEquals(1, numberOfFossils());
	}

	private int numberOfFossils() {
		return fossilStack.numberOfFossils().get().intValue();
	}

	private Fossil unanalyzedFossil() {
		Fossil fossil = Fossil.createWithQuality(Quality.LOW).andWithType(
				FOSSIL_STACK_TYPE);
		checkState(!fossil.isAnalyzed().get());
		return fossil;
	}
	
	@Test
	public void testNumberOfFossil_addAnalyzed() {
		fossilStack.add(analyzedFossil());
		assertEquals(1, numberOfFossils());
	}

	private Fossil analyzedFossil() {
		return unanalyzedFossil().analyze();
	}
	
	@Test
	public void testNumberOfFossil_fossilRemoved() {
		Fossil fossil;
		fossilStack.add(fossil=analyzedFossil());
		fossilStack.remove(fossil);
		assertEquals(0, numberOfFossils());
	}
}
