package edu.bsu.bonewars.core.model;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Map;

import react.Signal;

import com.google.common.collect.Maps;

import edu.bsu.bonewars.core.model.Fossil.Quality;
import edu.bsu.bonewars.core.model.Fossil.Type;

public final class PublishedFossilsArea {

	public static final class TrumpEvent {
		public final Player trumpedPlayer;

		private TrumpEvent(Player player) {
			this.trumpedPlayer = checkNotNull(player);
		}
	}

	public static PublishedFossilsArea create() {
		return new PublishedFossilsArea();
	}

	private final Map<Fossil.Type, FossilStack> map = Maps.newHashMap();
	private final Signal<TrumpEvent> playerWasTrumpedSignal = Signal.create();

	private PublishedFossilsArea() {
		addFossilStacks();
	}

	private void addFossilStacks() {
		for (Type type : Type.values()) {
			map.put(type, FossilStack.createOfType(type));
		}
	}

	public Signal<TrumpEvent> playerWasTrumpedSignal() {
		return playerWasTrumpedSignal;
	}

	public void publish(Fossil fossil) {
		checkState(isPublishable(fossil));
		handleTrumping(fossil);
		FossilStack fossilStack = map.get(fossil.type());
		fossilStack.add(fossil);
	}

	private void handleTrumping(Fossil fossil) {
		if (playerWasTrumped(fossil)) {
			Player trumpedPublisher = map.get(fossil.type()).getBestFossil()
					.get().owner();
			playerWasTrumpedSignal.emit(new TrumpEvent(trumpedPublisher));
		}
	}

	private boolean playerWasTrumped(Fossil fossil) {
		return hasTypeBeenPublish(fossil.type())//
				&& !isCurrentFossilOfSameQuality(fossil)//
				&& wasThePreviousFossilPublishedByADifferentPlayer(fossil);
	}

	private boolean isCurrentFossilOfSameQuality(Fossil fossil) {
		Fossil previousFossil = map.get(fossil.type()).getBestFossil().get();
		return previousFossil.quality().equals(fossil.quality());
	}

	private boolean wasThePreviousFossilPublishedByADifferentPlayer(
			Fossil fossil) {
		Player previousPublisher = map.get(fossil.type()).getBestFossil().get()
				.owner();
		return !previousPublisher.equals(fossil.owner());
	}

	public boolean isPublishable(Fossil fossil) {
		if (hasTypeBeenPublish(fossil.type())) {
			Fossil publishedFossil = map.get(fossil.type()).getBestFossil()
					.get();
			return fossil.quality().compareTo(publishedFossil.quality()) >= 0;
		} else {
			return true;
		}
	}

	public boolean hasTypeBeenPublish(Type type) {
		return !map.get(type).isEmpty();
	}

	public FossilStack getPublishedFossilsOfType(Type type) {
		return map.get(type);
	}
	
	public FossilStack getPublishedFossilsOfQuality(Quality quality){
		return map.get(quality);
	}

}
