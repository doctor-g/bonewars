package edu.bsu.bonewars.core.event;

public interface GameEvent {

	public interface Visitor<T> {
		T visit(ExcavationEvent event, Object... args);

		T visit(PublicationEvent publicationEvent, Object... args);
		
		T visit(BouncedPublishAttempt bouncedEvent, Object... args);

		public abstract class Adapter<T> implements Visitor<T> {

			@Override
			public T visit(ExcavationEvent event, Object... args) {
				return null;
			}

			@Override
			public T visit(PublicationEvent publicationEvent, Object... args) {
				return null;
			}

			@Override
			public T visit(BouncedPublishAttempt bouncedEvent, Object... args) {
				return null;
			}
		}
	}

	public <T> T accept(Visitor<T> visitor, Object... args);

}
