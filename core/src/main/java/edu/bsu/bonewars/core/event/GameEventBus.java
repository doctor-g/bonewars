package edu.bsu.bonewars.core.event;

import java.util.List;

import com.google.common.collect.Lists;

public final class GameEventBus {

	public static GameEventBus create() {
		return new GameEventBus();
	}

	private final List<GameEvent> q = Lists.newLinkedList();
	private final List<GameEventHandler> handlers = Lists.newArrayList();

	private GameEventBus() {
	}

	public GameEventBus add(GameEvent event) {
		q.add(event);
		return this;
	}

	public int size() {
		return q.size();
	}

	public GameEventBus register(GameEventHandler handler) {
		handlers.add(handler);
		return this;
	}

	public void update() {
		while (!q.isEmpty()) {
			GameEvent event = q.remove(0);
			dispatch(event);
		}
	}

	private void dispatch(GameEvent event) {
		for (GameEventHandler handler : handlers) {
			handler.handle(event);
		}
	}

}
