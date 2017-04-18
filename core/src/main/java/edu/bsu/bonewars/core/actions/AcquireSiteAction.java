package edu.bsu.bonewars.core.actions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import react.UnitSignal;
import react.UnitSlot;
import react.Value;
import react.ValueView;
import edu.bsu.bonewars.core.model.Funds;
import edu.bsu.bonewars.core.model.Game;
import edu.bsu.bonewars.core.model.Site;

public class AcquireSiteAction extends AbstractAction {

	public static AcquireSiteAction createWithSite(Site site) {
		return new AcquireSiteAction(site);
	}

	private UnitSignal actionCompleteSignal = new UnitSignal();

	private Value<Boolean> available = Value.create(false);
	public final Site site;

	public UnitSignal actionCompleteSignal() {
		return actionCompleteSignal;
	}

	private AcquireSiteAction(Site site) {
		this.site = checkNotNull(site);
		connectGameWorkerSelectionSignal();
		Game.currentGame().actionRegistry().registerAcquireSiteAction(this);
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
		if (!site.hasOwner().get()
				&& currentGame.workerSelectionModel().hasSelection()
				&& currentGame.workerSelectionModel().selection().isBigWorker()
				&& evaluateIfPlayerHasEnoughFunds()) {
			available.update(true);
		} else {
			available.update(false);
		}
	}

	private boolean evaluateIfPlayerHasEnoughFunds() {
		return Game.currentGame().currentPlayer().get().funds().get().toInt() >= Site.SITE_PRICE;
	}

	public void doAction() {
		checkState(available.get());
		Game game = Game.currentGame();
		site.setOwner(game.currentPlayer().get());
		checkState(game.workerSelectionModel().hasSelection());
		game.currentPlayer().get()
				.subtractFunds(Funds.valueOf(Site.SITE_PRICE));
		game.workerSelectionModel().selection().markAsWorked();
		game.workerSelectionModel().deselect();
		actionCompleteSignal.emit();
	}

	public ValueView<Boolean> available() {
		return available;
	}

	public Site site() {
		return site;
	}

}
