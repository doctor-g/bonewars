package edu.bsu.bonewars.core.actions;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import edu.bsu.bonewars.core.model.Funds;
import edu.bsu.bonewars.core.model.Game;
import edu.bsu.bonewars.core.model.Player;

public class RaiseFundsActionTest {

	private Game game;
	private RaiseFundsAction action;

	@Before
	public void setUp() {
		game = Game.create();
		this.action = RaiseFundsAction.create();
	}

	@Test
	public void testFundsAreIncreasedAfterTakingAction() {
		Player actingPlayer = game.currentPlayer().get();
		actingPlayer.bigWorker().select();
		Funds initial = actingPlayer.funds().get();
		action.doAction();
		Funds afterAction = actingPlayer.funds().get();
		Assert.assertEquals(RaiseFundsAction.AMOUNT_GAINED_THROUGH_ACTION,
				afterAction.subtract(initial));
	}

	@Test
	public void testActionStartsUnavailable() {
		Assert.assertFalse(action.available().get());
	}

	@Test
	public void testActionisAvailableWhenBigWorkerIsSelected() {
		Player actingPlayer = game.currentPlayer().get();
		actingPlayer.bigWorker().select();
		Assert.assertTrue(action.available().get());
	}

}
