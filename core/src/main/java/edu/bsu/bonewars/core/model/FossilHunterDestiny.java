package edu.bsu.bonewars.core.model;

import react.Connection;
import react.UnitSlot;

public final class FossilHunterDestiny extends AbstractDestiny {

	private static final FossilHunterDestiny SINGLETON = new FossilHunterDestiny();

	public static FossilHunterDestiny instance() {
		return SINGLETON;
	}

	private Connection connection;

	private FossilHunterDestiny() {
	}

	@Override
	public void run(Game game) {
		connection = game.onAuctionComplete().connect(new UnitSlot() {
			@Override
			public void onEmit() {
				connection.disconnect();
				connection = null;
				onComplete.emit(FossilHunterDestiny.this);
			}
		});
		game.doFossilHunterAuction();
	}
}
