package edu.bsu.bonewars.core.model;

import com.google.common.base.Objects;

public class Fame implements Comparable<Fame> {

	public static Fame valueOf(int fame) {
		return new Fame(fame);
	}

	private final int value;

	private Fame(int fame) {
		this.value = fame;
	}

	public Fame add(Fame fame) {
		return new Fame(this.value + fame.value);
	}

	public Fame subtract(Fame fame) {
		if (this.value - fame.value >= 0)
			return new Fame(this.value - fame.value);
		else
			return new Fame(0);
	}

	public int toInt() {
		return value;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (obj.getClass().equals(Fame.class)) {
			Fame other = (Fame) obj;
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

	@Override
	public int compareTo(Fame other) {
		return this.value - other.value;
	}

}
