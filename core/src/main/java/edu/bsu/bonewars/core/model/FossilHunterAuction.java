package edu.bsu.bonewars.core.model;

import java.util.Iterator;
import java.util.Random;

import react.UnitSignal;
import react.UnitSlot;
import react.Value;
import tripleplay.util.Randoms;
import edu.bsu.bonewars.core.model.Fossil.Quality;
import edu.bsu.bonewars.core.model.Fossil.Type;

public final class FossilHunterAuction {

	private static final Randoms randoms = Randoms.with(new Random());

	public static FossilHunterAuction create() {
		return new FossilHunterAuction();
	}

	private Iterator<Funds> bidAmountIterator = new Iterator<Funds>() {
		private static final int INITIAL = 0;
		private int previous = INITIAL;

		@Override
		public boolean hasNext() {
			return true;
		}

		@Override
		public Funds next() {
			int asInt = previous + 2;
			Funds bid = Funds.valueOf(asInt);
			previous = asInt;
			return bid;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	};

	public final Value<Funds> highBid = Value.create(null);
	public final Value<Player> highBidder = Value.create(null);
	public final Value<Funds> nextBidAmount = Value.create(bidAmountIterator
			.next());
	public final Fossil fossil = randomFossilOfRatherHighQuality();
	private final UnitSignal onBid = new UnitSignal();

	private FossilHunterAuction() {
	}

	private Fossil randomFossilOfRatherHighQuality() {
		return Fossil.createWithQuality(randomQuality())
				.andWithType(randomType()).analyze();
	}

	private static Quality randomQuality() {
		if (randoms.getProbability(0.5f)) {
			return Quality.MEDIUM;
		} else {
			return Quality.HIGH;
		}
	}

	private static Type randomType() {
		return Type.values()[randoms.getInt(Type.values().length)];
	}

	public boolean hasBids() {
		return highBid.get() != null;
	}

	public FossilHunterAuction bid(Player bidder) {
		if (hasEnoughMoney(bidder)) {
			updateBid();
			highBidder.update(bidder);
			onBid.emit();
			return this;
		} else {
			throw new IllegalStateException("Insufficient funds. Player has "
					+ bidder.funds().get() + " and bid is "
					+ nextBidAmount.get());
		}
	}

	private boolean hasEnoughMoney(Player p) {
		Funds marshFunds = p.funds().get();
		return marshFunds.compareTo(nextBidAmount.get()) > 0;
	}

	private void updateBid() {
		highBid.update(nextBidAmount.get());
		advanceNextBidAmount();
	}

	private void advanceNextBidAmount() {
		nextBidAmount.update(bidAmountIterator.next());
	}

	public FossilHunterAuction copeBid() {
		updateBid();
		highBidder.update(Game.currentGame().cope);
		return this;
	}

	public boolean canBid(Player marsh) {
		return hasEnoughMoney(marsh);
	}

	public void onBid(UnitSlot slot) {
		onBid.connect(slot);
	}

	public void complete() {
		if (hasBids()) {
			giveFossilToHighBidder();
			subtractFundsFromHighBidder();
		}
	}

	private void giveFossilToHighBidder() {
		highBidder.get().addFossil(fossil);
	}

	private void subtractFundsFromHighBidder() {
		highBidder.get().setFunds(
				highBidder.get().funds().get().subtract(highBid.get()));
	}

}
