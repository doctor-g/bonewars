package edu.bsu.bonewars.core.model;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static playn.core.PlayN.log;

import java.util.List;
import java.util.Random;

import playn.core.util.Callback;
import react.Signal;
import react.SignalView;
import react.Slot;
import react.UnitSignal;
import react.UnitSlot;
import react.Value;
import react.ValueView;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import edu.bsu.bonewars.core.actions.AcquireSiteAction;
import edu.bsu.bonewars.core.actions.AnalyzeAction;
import edu.bsu.bonewars.core.actions.AnalyzedPublishAction;
import edu.bsu.bonewars.core.actions.ExcavateAction;
import edu.bsu.bonewars.core.actions.UnanalyzedPublishedAction;
import edu.bsu.bonewars.core.event.GameEventBus;
import edu.bsu.bonewars.core.util.ExceptionThrowingOnFailureCallback;
import edu.bsu.bonewars.core.util.SingleSelectionModel;

public class Game {

	private static final Value<Integer> playedGames = Value.create(0);

	public static ValueView<Integer> playedGames() {
		return playedGames;
	}

	public static final int TOTAL_NUMBER_OF_FOSSILS = 56;
	public static final int NUMBER_OF_FOSSILS_PER_SITE = 6;
	public static final int NUMBER_OF_STARTING_SITES = 5;
	public static final int NUMBER_OF_STARTING_UNAVAILABLE_SITES = 3;
	public static final int NUMBER_OF_ROUNDS_PER_GAME = 8;
	public static final Fame FIRST_PUBLISH_BONUS = Fame.valueOf(1);
	public static final Fame FAME_TRUMP_STEAL_VALUE = Fame.valueOf(1);
	public static final Fame FAME_LOWER_QUALITY_VALUE = Fame.valueOf(1);

	public static Game create() {
		Game g = new Game();
		playedGames.update(playedGames.get() + 1);
		return g;
	}

	private static Game currentGame;
	private PublishedFossilsArea publishedFossilsArea = PublishedFossilsArea
			.create();
	private List<Site> sites = Lists.newArrayList();
	public Player marsh;
	public Player cope;
	private Value<Player> currentPlayer = Value.create(marsh);
	private SingleSelectionModel<Worker> workerSelection = SingleSelectionModel
			.create();
	private ActionRegistry actionRegistry = ActionRegistry.create();
	private SiteFactory siteFactory = SiteFactory.create();
	private Value<Integer> round = Value.create(1);
	private Signal<Site> onSiteAdded = Signal.create();
	private Destiny destinyPolicy = RepeatingFiniteDestiny
			.from(new RepeatingFiniteDestiny.DestinyGenerator() {
				@Override
				public List<? extends Destiny> generateDestinies() {
					return ImmutableList.of(NewSiteAvailableDestiny.instance(),
							StoryEventDestiny.instance(), //
							FossilHunterDestiny.instance());

				}
			});
	private Signal<Player> onGameOver = Signal.create();
	private UpkeepHandler upkeepHandler;
	private AuctionHandler auctionHandler;
	private StoryEventHandler storyEventHandler;
	private UnitSignal onAuctionComplete = new UnitSignal();
	private UnitSignal onWorkerUnderflow = new UnitSignal();

	public final GameEventBus eventBus = GameEventBus.create();

	private Game() {
		currentGame = this;
		initPlayers();
		initSites();
		setStartingOwnedSites();
		setUpRoundIncrementAfterDestinyEvent();
	}

	private void setStartingOwnedSites() {
		Random generator = new Random();
		int index = findAnIndexOfASiteWithNoOwner(generator);
		sites.get(index).setOwner(marsh);

		int anotherIndex = findAnIndexThatIsNotEqualToPreviousIndexAndThatASiteDoesNotHaveAnOwnerAt(
				generator, index);
		sites.get(anotherIndex).setOwner(cope);
	}

	private int findAnIndexOfASiteWithNoOwner(Random generator) {
		int index = (int) (generator.nextFloat() * sites.size());
		while (sites.get(index).hasOwner().get()) {
			index = (int) (generator.nextFloat() * sites.size());
		}
		return index;
	}

	private int findAnIndexThatIsNotEqualToPreviousIndexAndThatASiteDoesNotHaveAnOwnerAt(
			Random generator, int previousIndex) {
		int anotherIndex = previousIndex;
		while (anotherIndex == previousIndex
				|| sites.get(anotherIndex).hasOwner().get()) {
			anotherIndex = (int) (generator.nextFloat() * sites.size());
		}
		return anotherIndex;
	}

	private void initSites() {
		initAvailableSites();
	}

	private void initAvailableSites() {
		checkState(sites.isEmpty());
		for (int i = 0; i < NUMBER_OF_STARTING_SITES; i++) {
			addAvailableSite();
		}
	}

	private void setUpRoundIncrementAfterDestinyEvent() {
		destinyPolicy.onComplete().connect(new Slot<Destiny>() {
			@Override
			public void onEmit(Destiny destiny) {
				incrementRoundAndUpdateActivePlayer();
			}
		});
	}

	private void incrementRoundAndUpdateActivePlayer() {
		round.update(round.get() + 1);
		if (round.get() % 2 == 0) {
			Game.currentGame.currentPlayer.update(cope);
		} else
			Game.currentGame.currentPlayer.update(marsh);
	}

	public void addAvailableSite() {
		try {
			final Site site = siteFactory.next();
			AcquireSiteAction.createWithSite(site);
			ExcavateAction.createWithSite(site);
			sites.add(site);
			onSiteAdded.emit(site);
		} catch (SiteFactory.OutOfSiteException e) {
			// The site factory canna' take more of this, captain.
			log().warn("Ignoring attempt to add another site.");
		}
	}

	private void initPlayers() {
		marsh = Player.createMarsh();
		cope = Player.createCope();
		currentPlayer.update(marsh);
		registerPlayersFossilCollection(marsh);
		registerPlayersFossilCollection(cope);
	}

	private void registerPlayersFossilCollection(Player player) {
		for (Fossil.Type type : Fossil.Type.values()) {
			AnalyzeAction.create(player.fossilStack(type));
			AnalyzedPublishAction.create(player.fossilStack(type));
			UnanalyzedPublishedAction.create(player.fossilStack(type));
		}
	}

	public ValueView<Player> currentPlayer() {
		return currentPlayer;
	}

	public static Game currentGame() {
		return currentGame;
	}

	public PublishedFossilsArea publishedFossilsArea() {
		return publishedFossilsArea;
	}

	public ImmutableList<Site> sites() {
		return ImmutableList.copyOf(sites);
	}

	public Iterable<Site> availableSites() {
		return Iterables.filter(sites, new Predicate<Site>() {
			@Override
			public boolean apply(Site input) {
				return !input.hasOwner().get();
			}
		});
	}

	public ActionRegistry actionRegistry() {
		return actionRegistry;
	}

	public void wireUpAction(Action action) {
		action.actionCompleteSignal().connect(new UnitSlot() {
			@Override
			public void onEmit() {
				Player nonCurrentPlayer = getNonCurrentPlayer();

				if (nonCurrentPlayer.hasAvailableWorker()) {
					switchCurrentPlayer();
					checkForWorkerUnderflow();
				} else if (!currentPlayer.get().hasAvailableWorker()) {
					endRound();
				} else {
					checkForWorkerUnderflow();
				}

			}
		});
	}

	public Player getNonCurrentPlayer() {
		return currentPlayer.get().equals(marsh) ? cope : marsh;
	}

	public void endRound() {
		if (wasThisTheLastRound()) {
			endGame();
		} else {
			doEndOfRoundActionsStartingWithUpkeep();
		}
	}

	private boolean wasThisTheLastRound() {
		return round.get().intValue() == NUMBER_OF_ROUNDS_PER_GAME;
	}

	private void endGame() {
		if (marshHasMoreFame()) {
			endGameMarshWins();
		} else {
			endGameCopeWins();
		}
	}

	private boolean marshHasMoreFame() {
		return marsh.fame().get().compareTo(cope.fame().get()) > 0;
	}

	private void doEndOfRoundActionsStartingWithUpkeep() {
		if (upkeepHandler != null) {
			upkeepHandler
					.timeForUpkeep(new ExceptionThrowingOnFailureCallback<Void>() {
						@Override
						public void onSuccess(Void result) {
							doEndOfRoundDestiny();
						}
					});
		} else {
			doEndOfRoundDestiny();
		}
	}

	private void doEndOfRoundDestiny() {
		destinyPolicy.onComplete().connect(new Slot<Destiny>() {
			@Override
			public void onEmit(Destiny destiny) {
				enableWorkersToStartNextRound();
			}

			private void enableWorkersToStartNextRound() {
				marsh.markAllWorkersAsReady();
				cope.markAllWorkersAsReady();
			}
		});
		destinyPolicy.run(this);
	}

	private void switchCurrentPlayer() {
		Player playerToBeCurrentPlayer = getNonCurrentPlayer();
		currentPlayer.update(playerToBeCurrentPlayer);
	}

	public SingleSelectionModel<Worker> workerSelectionModel() {
		return workerSelection;
	}

	public ValueView<Integer> round() {
		return round;
	}

	public Player opponentOf(Player player) {
		checkNotNull(player);
		return player.isCope() ? marsh : cope;
	}

	public SignalView<Site> onSiteAdded() {
		return onSiteAdded;
	}

	public Game setDestinyPolicy(Destiny policy) {
		this.destinyPolicy = checkNotNull(policy);
		setUpRoundIncrementAfterDestinyEvent();
		return this;
	}

	public void doStoryEvent(StoryEvent storyEvent,
			final Callback<Void> onComplete) {
		storyEvent.apply();
		if (storyEventHandler != null) {
			storyEventHandler.handleStoryEvent(storyEvent,
					new ExceptionThrowingOnFailureCallback<Void>() {
						@Override
						public void onSuccess(Void result) {
							onComplete.onSuccess(result);
						}
					});
		} else {
			onComplete.onSuccess(null);
		}
	}

	public SignalView<Player> onGameOver() {
		return onGameOver;
	}

	public void setUpkeepHandler(UpkeepHandler handler) {
		this.upkeepHandler = checkNotNull(handler);
	}

	public void setAuctionHandler(AuctionHandler handler) {
		this.auctionHandler = checkNotNull(handler);
	}

	public void setStoryEventHandler(StoryEventHandler handler) {
		this.storyEventHandler = checkNotNull(handler);
	}

	public void endGameCopeWins() {
		onGameOver.emit(cope);
	}

	public void endGameMarshWins() {
		onGameOver.emit(marsh);
	}

	public void doFossilHunterAuction() {
		if (auctionHandler != null) {
			auctionHandler
					.timeForAuction(new ExceptionThrowingOnFailureCallback<Void>() {
						@Override
						public void onSuccess(Void result) {
							onAuctionComplete.emit();
						}
					});
		} else {
			onAuctionComplete.emit();
		}
	}

	public SignalView<Void> onAuctionComplete() {
		return onAuctionComplete;
	}

	private void checkForWorkerUnderflow() {
		boolean workerWasMarkedAsWorked = false;
		if (!actionRegistry
				.areAnyActionsAvailableForSmallWorkerOfCurrentPlayer()
				&& !currentPlayer.get().bigWorker().isReadyForWork().get()) {
			for (Worker worker : currentPlayer.get().littleWorkerCollection()) {
				if (worker.isReadyForWork().get()) {
					onWorkerUnderflow.emit();
					worker.markAsWorked();
					workerWasMarkedAsWorked = true;
				}
			}
		}
		if (workerWasMarkedAsWorked) {
			Player nonCurrentPlayer = getNonCurrentPlayer();

			if (nonCurrentPlayer.hasAvailableWorker()) {
				switchCurrentPlayer();
				checkForWorkerUnderflow();
			} else if (!currentPlayer.get().hasAvailableWorker()) {
				endRound();
			}
		}
	}

	public SignalView<Void> onWorkerUnderFlow() {
		return onWorkerUnderflow;
	}

	public interface StoryEventHandler {
		void handleStoryEvent(StoryEvent storyEvent, Callback<Void> onComplete);
	}

	public interface UpkeepHandler {
		void timeForUpkeep(Callback<Void> callback);
	}

	public interface AuctionHandler {
		void timeForAuction(Callback<Void> callback);
	}
}
