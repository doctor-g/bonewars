package edu.bsu.bonewars.core.model;

public final class NewSiteAvailableDestiny extends AbstractDestiny {

	private static final NewSiteAvailableDestiny SINGLETON = new NewSiteAvailableDestiny();

	public static NewSiteAvailableDestiny instance() {
		return SINGLETON;
	}

	private NewSiteAvailableDestiny() {
	}

	@Override
	public void run(Game game) {
		game.addAvailableSite();
		onComplete.emit(this);
	}

}
