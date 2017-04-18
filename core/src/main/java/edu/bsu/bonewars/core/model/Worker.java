package edu.bsu.bonewars.core.model;

import static com.google.common.base.Preconditions.checkState;
import react.Slot;
import react.UnitSlot;
import react.Value;
import react.ValueView;

public class Worker {

	private final Type type;
	private final Value<Boolean> isReadyForWork;
	private final Player boss;
	private final Value<Boolean> isSelectable;

	public static Worker createBigWorkerForBoss(Player boss) {
		return new Worker(Type.BIG_WORKER, boss);
	}

	public static Worker createLittleWorkerForBoss(Player boss) {
		return new Worker(Type.LITTLE_WORKER, boss);
	}

	private enum Type {
		BIG_WORKER, LITTLE_WORKER;
	}

	private Worker(Type type, Player boss) {
		this.boss = boss;
		this.type = type;
		isReadyForWork = new Value<Boolean>(true);
		isSelectable = new Value<Boolean>(false);
		updateSelectability();
		Game.currentGame().currentPlayer().connect(new Slot<Player>() {
			@Override
			public void onEmit(Player event) {
				updateSelectability();
			}
		});
		Game.currentGame().workerSelectionModel().enabledChange()
				.connect(new UnitSlot() {
					@Override
					public void onEmit() {
						updateSelectability();
					}
				});
	}

	private void updateSelectability() {
		checkState(Game.currentGame() != null, "No game has been created");
		final boolean bossIsCurrentPlayer = this.boss.equals(Game.currentGame()
				.currentPlayer().get());
		final boolean workedSelectionIsEnabled = Game.currentGame()
				.workerSelectionModel().isEnabled();
		final boolean workerIsReady = isReadyForWork.get();
		final boolean isSelectable = bossIsCurrentPlayer //
				&& workedSelectionIsEnabled //
				&& workerIsReady;
		this.isSelectable.update(isSelectable);
	}

	public ValueView<Boolean> isReadyForWork() {
		return isReadyForWork;
	}

	public void markAsWorked() {
		isReadyForWork.update(false);
		updateSelectability();
	}

	public void makeReadyForWork() {
		isReadyForWork.update(true);
		updateSelectability();
	}

	public Funds workerCost() {
		return Funds.valueOf(1);
	}

	public boolean isLittleWorker() {
		return type.equals(Type.LITTLE_WORKER);
	}

	public boolean isBigWorker() {
		return type.equals(Type.BIG_WORKER);
	}

	public void select() {
		checkState(isSelectable.get(),
				"This worker is not currently selectable.");
		Game.currentGame().workerSelectionModel().select(this);
	}

	public void deselect() {
		checkState(isSelected());
		Game.currentGame().workerSelectionModel().deselect();
	}

	public boolean isSelected() {
		Game game = Game.currentGame();
		if (!game.workerSelectionModel().hasSelection()) {
			return false;
		} else {
			return this.equals(game.workerSelectionModel().selection());
		}
	}

	public Player boss() {
		return boss;
	}

	public ValueView<Boolean> isSelectable() {
		return isSelectable;
	}
}
