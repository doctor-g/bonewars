package edu.bsu.bonewars.core.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class FundsTest {

	private static final Funds ZERO = Funds.valueOf(0);
	private static final Funds ONE = Funds.valueOf(1);
	private static final Funds TWO = Funds.valueOf(2);

	@Test
	public void testCompareTo_leftIsLess() {
		assertTrue(ZERO.compareTo(ONE) < 1);
	}

	@Test
	public void testSubtractUpTo_goingBelowZeroYieldsZero() {
		Funds actual = ONE.subtractUpTo(TWO);
		assertEquals(ZERO, actual);
	}
}
