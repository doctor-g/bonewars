package edu.bsu.bonewars.core.actions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import react.UnitSignal;
import react.UnitSlot;
import react.Value;
import react.ValueView;
import edu.bsu.bonewars.core.event.ExcavationEvent;
import edu.bsu.bonewars.core.model.Fossil;
import edu.bsu.bonewars.core.model.Game;
import edu.bsu.bonewars.core.model.Player;
import edu.bsu.bonewars.core.model.Site;

public class ExcavateAction extends AbstractAction {

	public static ExcavateAction createWithSite(Site aSite) {
		return new ExcavateAction(aSite);
	}

	private UnitSignal actionCompleteSignal = new UnitSignal();
	private Value<Boolean> available = Value.create(false);
	private final Site aSite;

	private ExcavateAction(Site aSite) {
		this.aSite = checkNotNull(aSite);
		connectGameWorkerSelectionSignal();
		Game.currentGame().actionRegistry().registerExcavateAction(this);
		Game.currentGame().wireUpAction(this);
	}

	public UnitSignal actionCompleteSignal() {
		return actionCompleteSignal;
	}

	public ValueView<Boolean> available() {
		return available;
	}

	public void doAction() {
		checkState(available.get());
		Game game = Game.currentGame();
		Player currentPlayer = game.currentPlayer().get();
		checkState(game.workerSelectionModel().hasSelection());
		Fossil excavatedFossil = aSite.excavateNextFossil();
		currentPlayer.addFossil(excavatedFossil);
		game.workerSelectionModel().selection().markAsWorked();
		game.workerSelectionModel().deselect();
		game.eventBus.add(new ExcavationEvent(currentPlayer,
				excavatedFossil, aSite));
		actionCompleteSignal.emit();
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
		if (currentGame.workerSelectionModel().hasSelection()//
				&& aSite.hasOwner().get()//
				&& aSite.hasNextFossil()//
				&& aSite.owner().equals(currentGame.currentPlayer().get())) {
			available.update(true);
		} else {
			available.update(false);
		}
	}

	public Site site() {
		return aSite;
	}

	public boolean isAvailableDisreguardingPlayerSelection() {
		// TODO Auto-generated method stub
		return aSite.hasOwner().get()//
				&& aSite.hasNextFossil()//
				&& aSite.owner().equals(
						Game.currentGame().currentPlayer().get());
	}

}
