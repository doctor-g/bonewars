package edu.bsu.bonewars.core.model;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.List;
import java.util.Map;

import react.Signal;
import react.SignalView;
import react.Value;
import react.ValueView;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import edu.bsu.bonewars.core.actions.HireWorkerAction;

public class Player {

	public static Player createMarsh() {
		return new Player(Type.MARSH);
	}

	public static Player createCope() {
		return new Player(Type.COPE);
	}

	private enum Type {
		MARSH, COPE;
	}

	public static final int MAX_NUMBER_OF_LITTLE_WORKERS = 4;
	public static final int STARTING_FUNDS_AMOUNT = 50;
	public static final int STARTING_FAME_AMOUNT = 0;
	public static final int NUMBER_OF_STARTING_LITTLE_WORKERS = 2;
	private Value<Fame> fame = new Value<Fame>(
			Fame.valueOf(STARTING_FAME_AMOUNT));
	private Value<Funds> funds = new Value<Funds>(
			Funds.valueOf(STARTING_FUNDS_AMOUNT));
	private Type type;
	private Map<Fossil.Type, FossilStack> fossilCollection = Maps.newHashMap();
	private List<Worker> littleWorkerCollection = Lists.newArrayList();
	private Signal<Player> onLittleWorkerCollectionChange = Signal.create();

	private Worker bigWorker;

	private Player(Type type) {
		this.type = type;
		bigWorker = Worker.createBigWorkerForBoss(this);
		initFossilCollection();
		initLittleWorkers();
		HireWorkerAction.create(this);
	}

	private void initFossilCollection() {
		for (Fossil.Type type : Fossil.Type.values()) {
			FossilStack fossilStack = FossilStack.createOfType(type);
			fossilCollection.put(type, fossilStack);
		}
	}

	private void initLittleWorkers() {
		checkState(littleWorkerCollection.isEmpty());
		for (int i = 0; i < NUMBER_OF_STARTING_LITTLE_WORKERS; i++) {
			addLittleWorker();
		}
	}

	public void resetWorkers() {
		for (Worker worker : littleWorkerCollection) {
			worker.makeReadyForWork();
		}
	}

	public void addFame(Fame fame) {
		this.fame.update(this.fame.get().add(fame));
	}

	public void subtractFame(Fame amountToSubtract) {
		this.fame.update(this.fame.get().subtract(amountToSubtract));
	}

	public ValueView<Funds> funds() {
		return funds;
	}

	public ValueView<Fame> fame() {
		return fame;
	}

	public void subtractFunds(Funds amountToSubtract) {
		this.funds.update(this.funds.get().subtract(amountToSubtract));
	}

	public void addFunds(Funds amountToAdd) {
		this.funds.update(this.funds.get().add(amountToAdd));
	}

	public FossilStack fossilStack(Fossil.Type type) {
		return fossilCollection.get(type);
	}

	public void addFossil(Fossil aFossil) {
		checkNotNull(aFossil);
		FossilStack stack = fossilCollection.get(aFossil.type());
		stack.add(aFossil);
		aFossil.setOwner(this);
	}

	public ImmutableList<Worker> littleWorkerCollection() {
		return ImmutableList.copyOf(littleWorkerCollection);
	}

	public Worker bigWorker() {
		return bigWorker;
	}

	public void addLittleWorker() {
		checkState(littleWorkerCollection.size() < MAX_NUMBER_OF_LITTLE_WORKERS);
		Worker workerToAdd = Worker.createLittleWorkerForBoss(this);
		littleWorkerCollection.add(workerToAdd);
		onLittleWorkerCollectionChange.emit(this);
	}

	public void fireLittleWorker() {
		littleWorkerCollection.remove(littleWorkerCollection.size() - 1);
		onLittleWorkerCollectionChange.emit(this);
	}

	public boolean isMarsh() {
		checkNotNull(type);
		return type.equals(Type.MARSH);
	}

	public boolean isCope() {
		checkNotNull(type);
		return type.equals(Type.COPE);
	}

	public boolean hasAvailableWorker() {
		if (bigWorker.isReadyForWork().get()) {
			return true;
		}
		for (Worker worker : littleWorkerCollection) {
			if (worker.isReadyForWork().get()) {
				return true;
			}
		}
		return false;
	}

	public void markAllWorkersAsReady() {
		bigWorker.makeReadyForWork();
		for (Worker littleWorker : littleWorkerCollection) {
			littleWorker.makeReadyForWork();
		}
	}

	public Iterable<Site> sites() {
		return Iterables.filter(Game.currentGame().sites(),
				new Predicate<Site>() {
					@Override
					public boolean apply(Site input) {
						return input.hasOwner().get()
								&& input.owner().equals(Player.this);
					}
				});
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (obj instanceof Player) {
			return equalAsLongAsTheyAreTheSamePlayerType((Player) obj);
		}
		return false;
	}

	private boolean equalAsLongAsTheyAreTheSamePlayerType(Player other) {
		return this.type == other.type;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(type);
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)//
				.add("type", type)//
				.add("fame", fame)//
				.add("funds", funds)//
				.toString();
	}

	public SignalView<Player> onLittleWorkerCollectionChange() {
		return onLittleWorkerCollectionChange;
	}

	public void setFunds(Funds newValue) {
		funds.update(newValue);
	}

	public String name() {
		return isMarsh() ? "Marsh" : "Cope";
	}

	public Player opponent() {
		if (isMarsh()) {
			return Game.currentGame().cope;
		} else
			return Game.currentGame().marsh;
	}
}
