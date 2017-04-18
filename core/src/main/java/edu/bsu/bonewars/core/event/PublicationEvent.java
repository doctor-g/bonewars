package edu.bsu.bonewars.core.event;

import edu.bsu.bonewars.core.model.Fame;
import edu.bsu.bonewars.core.model.Fossil;
import edu.bsu.bonewars.core.model.Player;

public final class PublicationEvent implements GameEvent {

	public static PublicationEvent create(Player player, Fossil fossil,
			Fame fame) {
		return new PublicationEvent(player, fossil, fame);
	}

	public static PublicationEvent createTrump(Player player, Fossil fossil,
			Fame gain, Fame loss) {
		PublicationEvent e = new PublicationEvent(player, fossil, gain);
		e.isTrump = true;
		e.loss = loss;
		return e;
	}

	public final Player player;
	public final Fossil fossil;
	public final Fame fame;
	public boolean isTrump = false;
	public Fame loss;

	private PublicationEvent(Player player, Fossil fossil, Fame fame) {
		this.player = player;
		this.fossil = fossil;
		this.fame = fame;
	}

	@Override
	public <T> T accept(Visitor<T> visitor, Object... args) {
		return visitor.visit(this, args);
	}

}
