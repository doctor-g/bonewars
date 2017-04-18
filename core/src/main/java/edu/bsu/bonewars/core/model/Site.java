package edu.bsu.bonewars.core.model;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.List;

import react.Value;
import react.ValueView;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class Site {

	public static final int SITE_PRICE = 10;
	private static int nextId = 0;

	public static Site createWithFossils(List<Fossil> fossils) {
		return new Site(fossils);
	}

	private Value<Integer> numberOfFossils;
	private Player owner;
	private Value<Boolean> hasOwner;
	private final int id;
	private List<Fossil> siteFossils = Lists.newArrayList();

	private Site(List<Fossil> fossils) {
		hasOwner = new Value<Boolean>(false);
		this.id = nextId++;
		siteFossils = fossils;
		numberOfFossils = Value.create(fossils.size());
	}

	public void setOwner(Player player) {
		owner = player;
		hasOwner.update(true);
	}

	public ValueView<Boolean> hasOwner() {
		return hasOwner;
	}

	public Player owner() {
		checkState(hasOwner().get(),
				"Please check that there is an owner before making this call.");
		return owner;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (obj.getClass().equals(Site.class)) {
			Site other = (Site) obj;
			return Objects.equal(this.id, other.id);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)//
				.add("owner", owner)//
				.toString();
	}

	public boolean hasNextFossil() {
		return !siteFossils.isEmpty();
	}

	public Fossil excavateNextFossil() {
		checkNotNull(siteFossils.get(0));
		numberOfFossils.update(siteFossils.size() - 1);
		return siteFossils.remove(0);
	}

	public boolean hasRequiredNumberOfFossils() {
		return siteFossils.size() == Game.NUMBER_OF_FOSSILS_PER_SITE;
	}

	public ValueView<Integer> numberOfFossils() {
		return numberOfFossils;
	}

	public ImmutableList<Fossil> fossils() {
		return ImmutableList.copyOf(siteFossils);
	}

}
