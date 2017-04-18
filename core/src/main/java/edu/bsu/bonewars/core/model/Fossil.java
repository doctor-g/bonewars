package edu.bsu.bonewars.core.model;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import react.Value;
import react.ValueView;

public class Fossil {

	public enum Quality {
		VERY_LOW(1), LOW(2), MEDIUM(3), HIGH(4);

		private final Fame fame;

		private Quality(int fame) {
			this.fame = Fame.valueOf(fame);
		}

		public Fame fame() {
			return fame;
		}
	}

	public enum Type {
		A, B, C, D, E, F, G, H;
	}

	private Value<Boolean> isAnalyzed = Value.create(false);

	public static FossilBuilder createWithQuality(Quality quality) {
		return new FossilBuilder(quality);
	}

	public static class FossilBuilder {

		private Quality quality;

		public Fossil andWithType(Type type) {
			return new Fossil(quality, type);
		}

		private FossilBuilder(Quality quality) {
			this.quality = quality;
		}
	}

	private final Quality quality;
	private final Type type;
	private Player owner;

	private Fossil(Quality quality, Type type) {
		this.quality = checkNotNull(quality);
		this.type = checkNotNull(type);
	}

	public Quality quality() {
		return quality;
	}

	public Type type() {
		return type;
	}

	public Player owner() {
		checkState(hasOwner(),
				"Please check that there is a owner before making this call.");
		return owner;
	}

	public boolean hasOwner() {
		return owner != null;
	}

	public Fossil analyze() {
		checkState(!isAnalyzed().get());
		isAnalyzed.update(true);
		return this;
	}

	public ValueView<Boolean> isAnalyzed() {
		return isAnalyzed;
	}

	public void setOwner(Player owner) {
		checkState(!hasOwner(), "Owner already set to " + this.owner);
		this.owner = checkNotNull(owner);
	}

	public int compareTo(Fossil fossil) {
		if (this == fossil) {
			return 0;
		}
		checkArgument(this.type.equals(fossil.type));
		if (isThisFossilNotAnalyzedButTheOtherFossilIs(fossil)) {
			return -1;
		} else if (isThisFossilAnalyzedButTheOtherFossilNot(fossil)) {
			return 1;
		} else if (areNotBothAnalyzed(fossil)) {
			return 0;
		} else {
			return this.quality().compareTo(fossil.quality());
		}
	}

	private boolean isThisFossilNotAnalyzedButTheOtherFossilIs(Fossil fossil) {
		return !this.isAnalyzed().get() && fossil.isAnalyzed().get();
	}

	private boolean isThisFossilAnalyzedButTheOtherFossilNot(Fossil fossil) {
		return this.isAnalyzed().get() && !fossil.isAnalyzed().get();
	}

	private boolean areNotBothAnalyzed(Fossil fossil) {
		return !this.isAnalyzed.get() && !fossil.isAnalyzed().get();
	}

}