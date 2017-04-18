package edu.bsu.bonewars.core.model;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.base.Objects;

public class Funds implements Comparable<Funds> {

	public static Funds valueOf(int funds) {
		return new Funds(funds);
	}

	private final int value;

	private Funds(int funds) {
		checkArgument(funds >= 0, "Funds cannot be negative.");
		this.value = funds;
	}

	public Funds add(Funds funds) {
		return valueOf(this.value + funds.value);
	}

	public Funds subtract(Funds funds) {
		return valueOf(this.value - funds.value);
	}

	public Funds subtractUpTo(Funds other) {
		return valueOf(Math.max(0, this.value - other.value));
	}

	public int asInt() {
		return value;
	}

	@Override
	public int compareTo(Funds other) {
		return this.value - other.value;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (obj.getClass().equals(Funds.class)) {
			Funds other = (Funds) obj;
			return this.value == other.value;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(value);
	}

	@Override
	public String toString() {
		return Objects//
				.toStringHelper(this)//
				.add("value", value)//
				.toString();
	}

	public int toInt() {
		return value;
	}

}
