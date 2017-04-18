package edu.bsu.bonewars.core.model;

import edu.bsu.bonewars.core.util.ExceptionThrowingOnFailureCallback;

public class StoryEventDestiny extends AbstractDestiny {

	public static StoryEventDestiny instance() {
		return new StoryEventDestiny();
	}

	private final StoryEventDeck deck = StoryEventDeck.create();

	@Override
	public void run(Game game) {
		game.doStoryEvent(deck.next(),
				new ExceptionThrowingOnFailureCallback<Void>() {
					@Override
					public void onSuccess(Void result) {
						onComplete.emit(StoryEventDestiny.this);
					}
				});
	}

}
