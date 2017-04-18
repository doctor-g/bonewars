package edu.bsu.bonewars.core.event;

import edu.bsu.bonewars.core.model.Fossil;
import edu.bsu.bonewars.core.model.Player;
import edu.bsu.bonewars.core.model.Site;

public class ExcavationEvent implements GameEvent {

	public final Player player;
	public final Fossil fossil;
	public final Site site;

	public ExcavationEvent(Player player, Fossil fossil, Site site) {
		this.player = player;
		this.fossil = fossil;
		this.site = site;
	}

	@Override
	public <T> T accept(Visitor<T> visitor, Object... args) {
		return visitor.visit(this, args);
	}

}
