package edu.bsu.bonewars.core;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import react.UnitSignal;
import react.UnitSlot;

public class ReactTest {

	private UnitSignal signal = new UnitSignal();
	private int count = 5;

	@Test(expected = IllegalStateException.class)
	public void testCyclicSignalsFiresException() {
		signal.connect(new UnitSlot() {
			@Override
			public void onEmit() {
				count--;
				if (count > 0) {
					signal.emit();
				}
			}
		});
		signal.emit();
		assertEquals(0, count);
	}

}
