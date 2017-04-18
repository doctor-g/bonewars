package edu.bsu.bonewars.core.model;

import static com.google.common.base.Preconditions.checkNotNull;
import react.Value;
import react.ValueView;

public class Upkeep {

	public static final int UPKEEP_SALARY_PER_WORKER = 2;

	public static Upkeep create() {
		return new Upkeep();
	}

	public final Options marsh;
	public final Options cope;
	private final Game game;

	private Upkeep() {
		game = Game.currentGame();
		marsh = new Options(game.marsh);
		cope = new Options(game.cope);
	}

	public final class Options {

		private Value<Integer> selection = Value.create(0);
		public final Player player;

		private Options(Player player) {
			this.player = checkNotNull(player);
			selection.update(max());
		}

		public int max() {
			if (thereAreMoreWorkersThanHeCanAfford()) {
				return fundsAsInt() / UPKEEP_SALARY_PER_WORKER;
			} else
				return numberOfWorkers();
		}

		private boolean thereAreMoreWorkersThanHeCanAfford() {
			return UPKEEP_SALARY_PER_WORKER * numberOfWorkers() > fundsAsInt();
		}

		private int fundsAsInt() {
			return player.funds().get().asInt();
		}

		private int numberOfWorkers() {
			return player.littleWorkerCollection().size();
		}

		public int retainedWorkers() {
			return selection.get().intValue();
		}

		public int salaryExpense() {
			return retainedWorkers() * UPKEEP_SALARY_PER_WORKER;
		}

		public void select(int i) {
			selection.update(i);
		}

		public ValueView<Integer> selection() {
			return selection;
		}

		public void fireWorkersBasedOnSelection() {
			while (thereAreWorkersToFire()) {
				player.fireLittleWorker();
			}
			player.subtractFunds(Funds.valueOf(salaryExpense()));
		}

		private boolean thereAreWorkersToFire() {
			return player.littleWorkerCollection().size() != selection().get()
					.intValue();
		}

	}
}
