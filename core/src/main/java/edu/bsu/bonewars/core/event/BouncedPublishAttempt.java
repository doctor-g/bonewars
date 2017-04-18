package edu.bsu.bonewars.core.event;

import edu.bsu.bonewars.core.model.Fame;
import edu.bsu.bonewars.core.model.Fossil;
import edu.bsu.bonewars.core.model.Player;

public class BouncedPublishAttempt implements GameEvent {

	public final Player player;
	public final Fossil fossil;
	public final Fame penalty;

	public BouncedPublishAttempt(Player player, Fossil fossil, Fame penalty) {
		this.player = player;
		this.fossil = fossil;
		this.penalty = penalty;
	}

	@Override
	public <T> T accept(Visitor<T> visitor, Object... args) {
		return visitor.visit(this, args);
	}

}
