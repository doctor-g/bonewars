package edu.bsu.bonewars.core.model;


public enum StoryEvent {

	MARSH_CHASED_BY_SIOUX(
			"Marsh was attacked by Sioux Indians! He pays $10 to replace lost supplies.") {
		@Override
		public void apply() {
			takeFromMarshUpTo(10);
		}
	},

	COPE_FAILS_SILVER_MINE(
			"Cope loses $10 for investing in a failed silver mine.") {
		@Override
		public void apply() {
			takeFromCopeUpTo(10);
		}
	},

	FOSSIL_INFLUX_ADD_FUNDS_TO_PLAYERS(
			"Marsh and Cope make paleontology famous! Both gain $4 in public support.") {
		@Override
		public void apply() {
			addFundsToMarshAndCope(4);
		}
	};

	public final String storyText;

	private StoryEvent(String storyText) {
		this.storyText = storyText;
	}

	public abstract void apply();

	private static void takeFromMarshUpTo(int amount) {
		Funds marshFunds = Game.currentGame().marsh.funds().get();
		if (marshFunds.asInt() < amount) {
			amount = marshFunds.asInt();
		}
		Game.currentGame().marsh.subtractFunds(Funds.valueOf(amount));
	}

	private static void takeFromCopeUpTo(int amount) {
		Funds copeFunds = Game.currentGame().cope.funds().get();
		if (copeFunds.asInt() < amount) {
			amount = copeFunds.asInt();
		}
		Game.currentGame().cope.subtractFunds(Funds.valueOf(amount));
	}

	private static void addFundsToMarshAndCope(int amount) {
		Game.currentGame().marsh.addFunds(Funds.valueOf(amount));
		Game.currentGame().cope.addFunds(Funds.valueOf(amount));
	}

}
