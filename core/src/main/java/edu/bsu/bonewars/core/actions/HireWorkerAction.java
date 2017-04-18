package edu.bsu.bonewars.core.actions;

import static com.google.common.base.Preconditions.checkState;
import react.UnitSignal;
import react.UnitSlot;
import react.Value;
import react.ValueView;
import edu.bsu.bonewars.core.model.Game;
import edu.bsu.bonewars.core.model.Player;

public class HireWorkerAction extends AbstractAction {

	private Player player;
	private Value<Boolean> available = Value.create(false);
	private UnitSignal actionCompleteSignal = new UnitSignal();

	public static HireWorkerAction create(Player player) {
		return new HireWorkerAction(player);
	}

	private HireWorkerAction(Player player) {
		this.player = player;
		connectGameWorkerSelectionSignal();
		Game.currentGame().actionRegistry().registerHireWorkerAction(this);
		Game.currentGame().wireUpAction(this);
	}

	private void connectGameWorkerSelectionSignal() {
		Game.currentGame().workerSelectionModel().onChange()
				.connect(new UnitSlot() {
					@Override
					public void onEmit() {
						evaluateIfActionIsEnabled();
					}
				});
	}

	private void evaluateIfActionIsEnabled() {
		Game currentGame = Game.currentGame();
		if (currentGame.workerSelectionModel().hasSelection()
				&& currentGame.workerSelectionModel().selection().isBigWorker()
				&& currentGame.currentPlayer().get().littleWorkerCollection().size() < Player.MAX_NUMBER_OF_LITTLE_WORKERS) {
			available.update(true);
		} else {
			available.update(false);
		}
	}

	@Override
	public UnitSignal actionCompleteSignal() {
		return actionCompleteSignal;
	}

	@Override
	public void doAction() {
		Game game = Game.currentGame();
		checkState(game.workerSelectionModel().hasSelection());
		player.addLittleWorker();
		game.workerSelectionModel().selection().markAsWorked();
		game.workerSelectionModel().deselect();
		actionCompleteSignal.emit();
	}

	@Override
	public ValueView<Boolean> available() {
		return available;
	}

	public Player player() {
		return player;
	}

}
