package edu.bsu.bonewars.core.model;

import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

public class StoryEventDeck {

	private static final Random random = new Random();

	public static StoryEventDeck create() {
		return new StoryEventDeck();
	}

	private final List<StoryEvent> originalList;
	private final List<StoryEvent> list;

	private StoryEventDeck() {
		originalList = Lists.newArrayList(StoryEvent.values());
		list = Lists.newArrayList(originalList);
	}

	public StoryEvent next() {
		if (list.isEmpty()) {
			list.addAll(originalList);
		}
		return list.remove(random.nextInt(list.size()));
	}
}
