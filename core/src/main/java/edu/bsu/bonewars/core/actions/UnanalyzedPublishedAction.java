package edu.bsu.bonewars.core.actions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import react.Connection;
import react.Slot;
import react.UnitSignal;
import react.UnitSlot;
import react.Value;
import react.ValueView;
import edu.bsu.bonewars.core.event.BouncedPublishAttempt;
import edu.bsu.bonewars.core.event.PublicationEvent;
import edu.bsu.bonewars.core.model.Fame;
import edu.bsu.bonewars.core.model.Fossil;
import edu.bsu.bonewars.core.model.FossilStack;
import edu.bsu.bonewars.core.model.Game;
import edu.bsu.bonewars.core.model.Player;
import edu.bsu.bonewars.core.model.PublishedFossilsArea;
import edu.bsu.bonewars.core.model.PublishedFossilsArea.TrumpEvent;

public class UnanalyzedPublishedAction extends AbstractAction {

	public static UnanalyzedPublishedAction create(FossilStack fossilStack) {
		return new UnanalyzedPublishedAction(fossilStack);
	}

	private FossilStack fossilStack;
	private Value<Boolean> available = Value.create(false);
	private UnitSignal actionCompleteSignal = new UnitSignal();

	private UnanalyzedPublishedAction(FossilStack fossilStack) {
		this.fossilStack = checkNotNull(fossilStack);
		connectGameWorkerSelectionSignal();
		final Game g = Game.currentGame();
		g.actionRegistry().registerUnanalyzedPublishAction(this);
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
		available.update(isThisActionAvailable());
	}

	private boolean isThisActionAvailable() {
		return bigWorkerIsSelected() //
				&& fossilStackHasUnanalyzedFossils() //
				&& currentPlayerIsFossilOwner();
	}

	private boolean bigWorkerIsSelected() {
		return Game.currentGame().workerSelectionModel().hasSelection() && //
				Game.currentGame().workerSelectionModel().selection()
						.isBigWorker();
	}

	private boolean fossilStackHasUnanalyzedFossils() {
		return fossilStack.numberOfUnanalyzedFossils().get() > 0;
	}

	private boolean currentPlayerIsFossilOwner() {
		return Game.currentGame().currentPlayer().get()
				.equals(fossilStack.getBestFossil().get().owner());
	}

	@Override
	public UnitSignal actionCompleteSignal() {
		return actionCompleteSignal;
	}

	private Game game;
	private Player player;
	private Fossil fossil;

	@Override
	public void doAction() {
		final Value<Boolean> trump = Value.create(false);
		checkState(available.get());
		Fame earnedFame = Fame.valueOf(0);
		boolean bounced = false;

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
		fossil = fossilStack.unanalyzedFossil();
		fossil.analyze();
		if (game.publishedFossilsArea().isPublishable(fossil)) {
			earnedFame = earnedFame.add(fossil.quality().fame());
			if (shouldGetFossilBonus()) {
				earnedFame = earnedFame.add(Game.FIRST_PUBLISH_BONUS);
			}
			game.publishedFossilsArea().publish(fossil);
		} else {
			player.subtractFame(Game.FAME_LOWER_QUALITY_VALUE);
			bounced = true;
		}
		player.addFame(earnedFame);
		fossilStack.remove(fossil);
		game.workerSelectionModel().selection().markAsWorked();
		game.workerSelectionModel().deselect();
		trumpSignalConnection.disconnect();
		if (bounced) {
			game.eventBus.add(new BouncedPublishAttempt(player, fossil,
					Game.FAME_LOWER_QUALITY_VALUE));
		} else if (trump.get()) {
			game.eventBus.add(PublicationEvent.createTrump(player, fossil,
					earnedFame, Game.FAME_TRUMP_STEAL_VALUE));
		} else {
			game.eventBus.add(PublicationEvent.create(player, fossil,
					earnedFame));
		}
		actionCompleteSignal.emit();
	}

	private boolean shouldGetFossilBonus() {
		FossilStack stack = game.publishedFossilsArea()
				.getPublishedFossilsOfType(fossil.type());
		return stack.isEmpty();
	}

	public ValueView<Boolean> available() {
		return available;
	}

	public FossilStack fossilStack() {
		return fossilStack;
	}

}
