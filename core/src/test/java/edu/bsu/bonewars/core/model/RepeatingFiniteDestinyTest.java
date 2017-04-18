package edu.bsu.bonewars.core.model;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.Test;

import react.Slot;

import com.google.common.collect.ImmutableList;

import edu.bsu.bonewars.core.model.RepeatingFiniteDestiny.DestinyGenerator;

public class RepeatingFiniteDestinyTest {

	@Test
	public void testRunProxiesDestiny_oneDestiny() {
		final CountingDestiny destiny = new CountingDestiny();
		DestinyGenerator gen = new DestinyGenerator() {
			@Override
			public List<? extends Destiny> generateDestinies() {
				return ImmutableList.of(destiny);
			}
		};
		RepeatingFiniteDestiny policy = RepeatingFiniteDestiny.from(gen);
		policy.run(mock(Game.class));
		assertEquals(1, destiny.count);
	}

	@Test
	public void testBothDestiniesRun_twoDestinies() {
		final CountingDestiny[] destinies = new CountingDestiny[] {
				new CountingDestiny(), new CountingDestiny() };
		RepeatingFiniteDestiny policy = RepeatingFiniteDestiny
				.from(new DestinyGenerator() {
					@Override
					public List<? extends Destiny> generateDestinies() {
						return ImmutableList.of(destinies[0], destinies[1]);
					}
				});
		for (int i = 0; i < destinies.length; i++) {
			policy.run(mock(Game.class));
		}
		for (int i = 0; i < destinies.length; i++) {
			assertEquals(1, destinies[i].count);
		}
	}

	@Test
	public void testBothDestiniesRunTwice_twoDestinies() {
		final CountingDestiny[] destinies = new CountingDestiny[] {
				new CountingDestiny(), new CountingDestiny() };
		RepeatingFiniteDestiny policy = RepeatingFiniteDestiny
				.from(new DestinyGenerator() {
					@Override
					public List<? extends Destiny> generateDestinies() {
						return ImmutableList.of(destinies[0], destinies[1]);
					}
				});
		for (int i = 0; i < destinies.length * 2; i++) {
			policy.run(mock(Game.class));
		}
		for (int i = 0; i < destinies.length; i++) {
			assertEquals(2, destinies[i].count);
		}
	}

	@Test
	public void testADestinyOnlyFiresNotificationOnce_executedOnce() {
		final Destiny destiny = new AbstractDestiny() {
			@Override
			public void run(Game game) {
				onComplete.emit(this);
			}
		};
		RepeatingFiniteDestiny policy = RepeatingFiniteDestiny
				.from(new DestinyGenerator() {
					@Override
					public List<? extends Destiny> generateDestinies() {
						return ImmutableList.of(destiny);
					}
				});
		@SuppressWarnings("unchecked")
		Slot<Destiny> slot = mock(Slot.class);
		policy.onComplete().connect(slot);
		policy.run(mock(Game.class));
		verify(slot).onEmit(destiny);
	}

	@Test
	public void testADestinyOnlyFiresNotificationOnce_executedTwice() {
		final Destiny destiny = new AbstractDestiny() {
			@Override
			public void run(Game game) {
				onComplete.emit(this);
			}
		};
		RepeatingFiniteDestiny policy = RepeatingFiniteDestiny
				.from(new DestinyGenerator() {
					@Override
					public List<? extends Destiny> generateDestinies() {
						return ImmutableList.of(destiny);
					}
				});
		@SuppressWarnings("unchecked")
		Slot<Destiny> slot = mock(Slot.class);
		policy.onComplete().connect(slot);
		for (int i = 0; i < 2; i++) {
			policy.run(mock(Game.class));
		}
		verify(slot, times(2)).onEmit(destiny);
	}

}

final class CountingDestiny extends AbstractDestiny {

	public int count = 0;

	@Override
	public void run(Game game) {
		onComplete.emit(this);
		count++;
	}

}
