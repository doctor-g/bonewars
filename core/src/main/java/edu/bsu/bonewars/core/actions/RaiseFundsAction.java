package edu.bsu.bonewars.core.actions;

import static com.google.common.base.Preconditions.checkState;
import react.UnitSignal;
import react.UnitSlot;
import react.Value;
import react.ValueView;
import edu.bsu.bonewars.core.model.Funds;
import edu.bsu.bonewars.core.model.Game;

public class RaiseFundsAction extends AbstractAction {

	public static RaiseFundsAction create() {
		return new RaiseFundsAction();
	}

	public static final Funds AMOUNT_GAINED_THROUGH_ACTION = Funds.valueOf(15);

	private Value<Boolean> available = Value.create(false);
	private UnitSignal actionCompleteSignal = new UnitSignal();

	public UnitSignal actionCompleteSignal() {
		return actionCompleteSignal;
	}

	private RaiseFundsAction() {
		connectGameWorkerSelectionSignal();
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
				&& currentGame.workerSelectionModel().selection().isBigWorker()) {
			available.update(true);
		} else {
			available.update(false);
		}
	}

	public void doAction() {
		Game game = Game.currentGame();
		checkState(available.get());
		game.currentPlayer().get().addFunds(AMOUNT_GAINED_THROUGH_ACTION);
		game.workerSelectionModel().selection().markAsWorked();
		game.workerSelectionModel().deselect();
		actionCompleteSignal.emit();
	}

	public ValueView<Boolean> available() {
		return available;
	}

}
