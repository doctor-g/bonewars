package edu.bsu.bonewars.core.model;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.List;

import react.Value;
import react.ValueView;
import react.ValueView.Listener;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import edu.bsu.bonewars.core.model.Fossil.Type;

public class FossilStack {
	public static FossilStack createOfType(Type type) {
		return new FossilStack(type);
	}

	private final Type type;
	private List<Fossil> fossils;
	private Value<Fossil> bestFossil;
	private Value<Integer> numberOfUnanalyzedFossils;
	private Value<Integer> totalFossils = Value.create(0);

	private FossilStack(Type type) {
		this.type = type;
		fossils = Lists.newArrayList();
		bestFossil = Value.create(null);
		numberOfUnanalyzedFossils = new Value<Integer>(0);
	}

	public void add(Fossil fossil) {
		checkState(fossil.type().equals(this.type));
		fossils.add(checkNotNull(fossil));
		connectFossilOnAnalyzed(fossil);
		sortFossils();
		updateNumberOfUnanalyzedFossils();
		updateBestFossil();
		totalFossils.update(totalFossils.get() + 1);
	}

	private void connectFossilOnAnalyzed(Fossil fossil) {
		fossil.isAnalyzed().connect(new Listener<Boolean>() {
			@Override
			public void onChange(Boolean value, Boolean oldValue) {
				updateNumberOfUnanalyzedFossils();
				sortFossils();
				updateBestFossil();
			}
		});
	}

	private void updateNumberOfUnanalyzedFossils() {
		int count = 0;
		for (Fossil aFossil : fossils) {
			if (!aFossil.isAnalyzed().get())
				count++;
		}
		numberOfUnanalyzedFossils.update(count);
	}

	public ImmutableList<Fossil> getAllFossils() {
		return ImmutableList.copyOf(fossils);
	}

	public boolean hasBestFossil() {
		return bestFossil.get() != null;
	}

	public ValueView<Fossil> getBestFossil() {
		return bestFossil;
	}

	public ValueView<Integer> numberOfUnanalyzedFossils() {
		return numberOfUnanalyzedFossils;
	}

	public boolean isEmpty() {
		return fossils.isEmpty();
	}

	public boolean contains(Fossil fossil) {
		return fossils.contains(fossil);
	}

	public void remove(Fossil fossil) {
		checkState(contains(fossil));
		fossils.remove(fossil);
		updateBestFossil();
		updateNumberOfUnanalyzedFossils();
		totalFossils.update(totalFossils.get() - 1);
		checkState(totalFossils.get() >= 0);
	}

	private void updateBestFossil() {
		if (isEmpty()) {
			bestFossil.update(null);
		} else {
			bestFossil.update(fossils.get(fossils.size() - 1));
		}
	}

	public Type type() {
		return type;
	}

	private void sortFossils() {
		if (fossils.size() <= 1) {
			return;
		}
		for (int i = 0; i < fossils.size(); i++) {
			Fossil lowestFossil = fossils.get(i);
			for (int j = i + 1; j < fossils.size(); j++) {
				if (fossils.get(i).compareTo(fossils.get(j)) > 0) {
					lowestFossil = fossils.get(j);
				}
			}
			swapFossils(i, fossils.indexOf(lowestFossil));
		}
	}

	private void swapFossils(int indexLow, int indexHigh) {
		checkArgument(indexLow <= indexHigh);
		if (indexLow == indexHigh) {
			return;
		}
		Fossil temp = fossils.get(indexLow);
		fossils.set(indexLow, fossils.get(indexHigh));
		fossils.set(indexHigh, temp);
	}

	public Fossil unanalyzedFossil() {
		checkArgument(hasAnUnanalyzedFossil());
		for (Fossil aFossil : fossils) {
			if (!aFossil.isAnalyzed().get()) {
				return aFossil;
			}
		}
		throw new IllegalStateException();
	}

	public boolean hasAnUnanalyzedFossil() {
		return numberOfUnanalyzedFossils.get() > 0;
	}

	public ValueView<Integer> numberOfFossils() {
		return totalFossils;
	}

}
