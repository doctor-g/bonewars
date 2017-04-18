package edu.bsu.bonewars.core.model;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.List;
import java.util.Random;

import react.Connection;
import react.Slot;

import com.google.common.collect.Lists;

public final class RepeatingFiniteDestiny extends AbstractDestiny {

	public interface DestinyGenerator {
		List<? extends Destiny> generateDestinies();
	}

	private static final Random RANDOM = new Random();

	public static RepeatingFiniteDestiny from(DestinyGenerator filler) {
		return new RepeatingFiniteDestiny(filler);
	}

	private final DestinyGenerator filler;
	private final List<Destiny> unusedDestinies = Lists.newArrayList();

	private RepeatingFiniteDestiny(DestinyGenerator filler) {
		this.filler = checkNotNull(filler);
	}

	@Override
	public void run(Game game) {
		if (unusedDestinies.isEmpty()) {
			unusedDestinies.addAll(filler.generateDestinies());
		}
		checkState(!unusedDestinies.isEmpty());

		int index = RANDOM.nextInt(unusedDestinies.size());
		final Destiny destiny = unusedDestinies.remove(index);
		fireCompleteWhenThisDestinyIsComplete(destiny);
		destiny.run(game);
	}

	private Connection connection;

	private void fireCompleteWhenThisDestinyIsComplete(Destiny destiny) {
		checkNotNull(destiny);
		connection = destiny.onComplete().connect(new Slot<Destiny>() {
			@Override
			public void onEmit(Destiny destiny) {
				connection.disconnect();
				connection = null;
				onComplete.emit(destiny);
			}
		});
	}

}
