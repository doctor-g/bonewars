package edu.bsu.bonewars.core.actions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import react.Connection;
import react.Slot;
import react.UnitSignal;
import react.UnitSlot;
import react.Value;
import react.ValueView;
import edu.bsu.bonewars.core.event.PublicationEvent;
import edu.bsu.bonewars.core.model.Fame;
import edu.bsu.bonewars.core.model.Fossil;
import edu.bsu.bonewars.core.model.FossilStack;
import edu.bsu.bonewars.core.model.Game;
import edu.bsu.bonewars.core.model.Player;
import edu.bsu.bonewars.core.model.PublishedFossilsArea;
import edu.bsu.bonewars.core.model.PublishedFossilsArea.TrumpEvent;

public class AnalyzedPublishAction extends AbstractAction {

	public static AnalyzedPublishAction create(FossilStack fossilStack) {
		return new AnalyzedPublishAction(fossilStack);
	}

	private FossilStack fossilStack;
	private Value<Boolean> available = Value.create(false);
	private UnitSignal actionCompleteSignal = new UnitSignal();

	private AnalyzedPublishAction(FossilStack fossilStack) {
		this.fossilStack = checkNotNull(fossilStack);
		connectGameWorkerSelectionSignal();
		final Game g = Game.currentGame();
		g.actionRegistry().registerAnalyzedPublishAction(this);
		g.wireUpAction(this);
	}

	private void connectGameWorkerSelectionSignal() {
		Game.currentGame().workerSelectionModel().onChange()
				.connect(new UnitSlot() {
					@Override
					public void onEmit() {
						updateAvailability();
					}
				});
	}

	private void updateAvailability() {
		if (Game.currentGame().workerSelectionModel().hasSelection() //
				&& Game.currentGame().workerSelectionModel().selection()
						.isBigWorker() //
				&& fossilStack.hasBestFossil() //
				&& Game.currentGame().publishedFossilsArea()
						.isPublishable(fossilStack.getBestFossil().get()) //
				&& fossilStack.getBestFossil().get().isAnalyzed().get()//
				&& Game.currentGame().currentPlayer().get()
						.equals(fossilStack.getBestFossil().get().owner())) {
			available.update(true);
		} else {
			available.update(false);
		}
	}

	@Override
	public UnitSignal actionCompleteSignal() {
		return actionCompleteSignal;
	}

	private Game game;
	private Player player;
	private Fossil bestFossil;

	@Override
	public void doAction() {
		final Value<Boolean> trump = Value.create(false);
		Fame earnedFame = Fame.valueOf(0);
		checkState(available.get());
		game = Game.currentGame();
		Connection trumpSignalConnection = game.publishedFossilsArea()
				.playerWasTrumpedSignal()
				.connect(new Slot<PublishedFossilsArea.TrumpEvent>() {
					@Override
					public void onEmit(TrumpEvent trumpEvent) {
						trump.update(true);
						trumpEvent.trumpedPlayer
								.subtractFame(Game.FAME_TRUMP_STEAL_VALUE);
						game.opponentOf(trumpEvent.trumpedPlayer).addFame(
								Game.FAME_TRUMP_STEAL_VALUE);
					}
				});
		player = game.currentPlayer().get();
		bestFossil = fossilStack.getBestFossil().get();
		earnedFame = earnedFame.add(bestFossil.quality().fame());
		if (shouldGetFossilBonus()) {
			earnedFame = earnedFame.add(Fame.valueOf(1));
		}
		player.addFame(earnedFame);
		fossilStack.remove(bestFossil);
		game.publishedFossilsArea().publish(bestFossil);
		game.workerSelectionModel().selection().markAsWorked();
		game.workerSelectionModel().deselect();
		trumpSignalConnection.disconnect();
		if (trump.get()) {
			game.eventBus.add(PublicationEvent.createTrump(player, bestFossil,
					earnedFame, Game.FAME_TRUMP_STEAL_VALUE));
		} else {
			game.eventBus.add(PublicationEvent.create(player, bestFossil,
					earnedFame));
		}
		actionCompleteSignal.emit();
	}

	private boolean shouldGetFossilBonus() {
		FossilStack stack = game.publishedFossilsArea()
				.getPublishedFossilsOfType(bestFossil.type());
		return stack.isEmpty();
	}

	public ValueView<Boolean> available() {
		return available;
	}

	public FossilStack fossilStack() {
		return fossilStack;
	}
}