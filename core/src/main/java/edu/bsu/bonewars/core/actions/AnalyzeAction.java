package edu.bsu.bonewars.core.actions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import react.UnitSignal;
import react.UnitSlot;
import react.Value;
import react.ValueView;
import edu.bsu.bonewars.core.model.FossilStack;
import edu.bsu.bonewars.core.model.Game;

public class AnalyzeAction extends AbstractAction {

	public static AnalyzeAction create(FossilStack fossilStack) {
		return new AnalyzeAction(fossilStack);
	}

	private FossilStack fossilStack;
	private Value<Boolean> available = Value.create(false);
	private UnitSignal actionCompleteSignal = new UnitSignal();

	private AnalyzeAction(FossilStack fossilStack) {
		this.fossilStack = checkNotNull(fossilStack);
		connectGameWorkerSelectionSignal();
		Game.currentGame().actionRegistry().registerAnalyzeAction(this);
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
		if (!fossilStack.isEmpty() && fossilStack.hasAnUnanalyzedFossil() && fossilStack.unanalyzedFossil().hasOwner()
				&& currentGame.workerSelectionModel().hasSelection() && currentGame.currentPlayer().get().equals(fossilStack.unanalyzedFossil().owner())) {
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
		checkNotNull(fossilStack);
		checkState(available.get());
		checkState(fossilStack.hasAnUnanalyzedFossil());
		fossilStack.unanalyzedFossil().analyze();
		game.workerSelectionModel().selection().markAsWorked();
		game.workerSelectionModel().deselect();
		actionCompleteSignal.emit();
	}

	public ValueView<Boolean> available() {
		return available;
	}
	
	public FossilStack fossilStack(){
		return fossilStack;
	}

	public boolean isAvailableDisreguardingPlayerSelection() {
		return !fossilStack.isEmpty() && fossilStack.hasAnUnanalyzedFossil() && fossilStack.unanalyzedFossil().hasOwner()
		&& Game.currentGame().currentPlayer().get().equals(fossilStack.unanalyzedFossil().owner());
	}
}
