package edu.bsu.bonewars.core.command;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

import edu.bsu.bonewars.core.event.GameEvent;
import edu.bsu.bonewars.core.event.GameEventBus;
import edu.bsu.bonewars.core.event.GameEventHandler;

public class GameEventBusTest {

	private GameEventBus q;

	@Before
	public void setUp() {
		q = GameEventBus.create();
	}

	@Test
	public void testSize_oneAdded_sizeIsOne() {
		q.add(mock(GameEvent.class));
		assertEquals(1, q.size());
	}

	@Test
	public void testRegister_registerAListener_itIsNotified() {
		GameEventHandler handler = mock(GameEventHandler.class);
		q.register(handler);
		GameEvent event = mock(GameEvent.class);
		q.add(event);
		q.update();
		verify(handler).handle(event);
	}
}
