package edu.bsu.bonewars.core.model;

import static com.google.common.base.Preconditions.checkState;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import playn.core.PlayN;
import playn.core.StubPlatform;
import playn.core.util.Callback;
import react.Slot;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import edu.bsu.bonewars.core.actions.AcquireSiteAction;
import edu.bsu.bonewars.core.actions.RaiseFundsAction;
import edu.bsu.bonewars.core.model.RepeatingFiniteDestiny.DestinyGenerator;
import edu.bsu.bonewars.core.util.IgnoreSuccessCallback;

public class GameTest {

	private Game game;

	@Before
	public void setUp() {
		game = Game.create();
		PlayN.setPlatform(new StubPlatform() {

		});
	}

	@Test
	public void testGameStartsWithNoWorkersSelected() {
		assertFalse(game.workerSelectionModel().hasSelection());
	}

	@Test
	public void testSelectedWorkerCurrentWorker() {
		Worker currentPlayersHighLevelWorker = game.currentPlayer().get()
				.bigWorker();
		currentPlayersHighLevelWorker.select();
		assertEquals(currentPlayersHighLevelWorker, game.workerSelectionModel()
				.selection());
	}

	@Test(expected = IllegalStateException.class)
	public void testAccessingWorkerWhenNoneIsSelectedThrowsException() {
		game.workerSelectionModel().selection();
	}

	@Test
	public void testActionForASiteIsForThatSite() {
		Site availableSite = game.sites().get(0);
		AcquireSiteAction action = game.actionRegistry()
				.getAcquireSiteActionForSite(availableSite);
		assertEquals(availableSite, action.site);
	}

	@Test
	public void testCurrentPlayerDoesNotChangeWhenOtherPlayerHasUsedAllOfTheirWorkers() {
		markLittleWorkersWorkedFor(game.marsh);
		raiseFundsFor(game.marsh);
		Player playerBeforeAction = game.currentPlayer().get();
		raiseFundsFor(game.cope);
		Player playerAfterAction = game.currentPlayer().get();
		assertEquals(playerBeforeAction, playerAfterAction);
	}

	private void raiseFundsFor(Player player) {
		checkState(game.currentPlayer().get().equals(player));
		RaiseFundsAction action = RaiseFundsAction.create();
		player.bigWorker().select();
		action.doAction();
	}

	@Test
	public void testThatRoundIsIncrementedWhenNoPlayersHaveAnAvailableWorkerLeft() {
		markLittleWorkersWorkedFor(game.marsh);
		raiseFundsFor(game.marsh);
		markLittleWorkersWorkedFor(game.cope);
		raiseFundsFor(game.cope);
		assertEquals(2, (int) game.round().get());
	}

	private void markLittleWorkersWorkedFor(Player player) {
		for (Worker worker : player.littleWorkerCollection()) {
			worker.markAsWorked();
		}
	}

	@Test
	public void testSignalFiredWhenSiteIsMadeAvailable() {
		@SuppressWarnings("unchecked")
		Slot<Site> slot = mock(Slot.class);
		game.onSiteAdded().connect(slot);
		game.addAvailableSite();
		verify(slot).onEmit(any(Site.class));
	}

	@Test
	public void testMarshStartsWithOneSite() {
		assertEquals(1, Iterables.size(game.marsh.sites()));
	}

	@Test
	public void testCopeStartsWithOneSite() {
		assertEquals(1, Iterables.size(game.cope.sites()));
	}

	// Suppressing unchecked warnings for Matchers.any(Callback<T>)
	@SuppressWarnings("unchecked")
	@Test
	public void testUpkeepHandlerNotification() {
		Game.UpkeepHandler handler = mock(Game.UpkeepHandler.class);
		game.setUpkeepHandler(handler);
		game.endRound();
		verify(handler).timeForUpkeep(any(Callback.class));
	}

	@Test
	public void testRoundIsAdvancedWhenLittleWorkersHaveNothingToDoForBothMarshAndCope() {
		for (Site aSite : Game.currentGame().sites()) {
			while (aSite.hasNextFossil()) {
				aSite.excavateNextFossil();
			}
		}
		RaiseFundsAction action = RaiseFundsAction.create();
		Game.currentGame().marsh.bigWorker().select();
		action.doAction();
		Game.currentGame().cope.bigWorker().select();
		action.doAction();
		assertEquals(2, (int) Game.currentGame().round().get());
	}

	// Suppressing unchecked warnings for Matchers.any(Callback<T>)
	@SuppressWarnings("unchecked")
	@Test
	public void testStoryEventHandlerNotification() {
		StoryEvent event = mock(StoryEvent.class);
		Game.StoryEventHandler handler = mock(Game.StoryEventHandler.class);
		game.setStoryEventHandler(handler);
		game.doStoryEvent(event, new IgnoreSuccessCallback());
		verify(handler).handleStoryEvent(eq(event), any(Callback.class));
	}

	@Test
	public void testRoundIncrementsAfterEveryEndOfRound() {
		checkAllRounds();
	}

	@Test
	public void testRoundIncrementsAfterEveryEndOfRound_onlyNewSiteEvents() {
		game.setDestinyPolicy(RepeatingFiniteDestiny
				.from(new DestinyGenerator() {
					@Override
					public List<? extends Destiny> generateDestinies() {
						return ImmutableList.of(NewSiteAvailableDestiny
								.instance());
					}
				}));
		checkAllRounds();
	}

	@Test
	public void testRoundIncrementAfterEveryEndOfRound_nullDestiny() {
		Destiny destiny = new AbstractDestiny() {
			@Override
			public void run(Game game) {
				onComplete.emit(this);
			}
		};
		game.setDestinyPolicy(destiny);
		checkAllRounds();
	}

	private void checkAllRounds() {
		for (int i = 1; i <= Game.NUMBER_OF_ROUNDS_PER_GAME; i++) {
			assertEquals(i, game.round().get().intValue());
			game.endRound();
		}
	}
	
	@Test
	public void testCountPlayedGames_incrementsOnCreatingNewGame() {
		@SuppressWarnings("unchecked")
		Slot<Integer> slot = mock(Slot.class);
		Game.playedGames().connect(slot);
		Integer played = Game.playedGames().get();
		Integer oneMore = played + 1;
		game = Game.create();
		verify(slot).onChange(oneMore, played);
	}
}
