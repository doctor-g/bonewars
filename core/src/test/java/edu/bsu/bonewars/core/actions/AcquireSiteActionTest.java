package edu.bsu.bonewars.core.actions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import edu.bsu.bonewars.core.model.Funds;
import edu.bsu.bonewars.core.model.Game;
import edu.bsu.bonewars.core.model.Player;
import edu.bsu.bonewars.core.model.Site;
import edu.bsu.bonewars.core.model.Worker;

public class AcquireSiteActionTest {

	private Game game;
	private Site site;
	private AcquireSiteAction action;

	@Before
	public void setup() {
		game = Game.create();
		site = nextAvailableSite();
		action = game.actionRegistry().getAcquireSiteActionForSite(site);
	}

	private Site nextAvailableSite() {
		return game.availableSites().iterator().next();
	}

	@Test
	public void testActionBecomesAvailableWhenMarshWorkerIsMadeCurrentWorker() {
		selectBigWorker();
		assertTrue(action.available().get());
	}

	@Test
	public void testThatSiteIsOwnedByMarshAfterAcquiringSite() {
		selectBigWorker();
		action.doAction();
		assertTrue(site.owner().equals(game.marsh));
	}

	@Test
	public void testThatCurrentPlayerLosesFundsAfterASiteIsAcquired() {
		selectBigWorker();
		Player player = Game.currentGame().currentPlayer().get();
		int playerFundBeforePurchasingSite = player.funds().get().asInt();
		action.doAction();
		int playerFundsAfterPurchasingSite = player.funds().get().asInt();
		assertEquals(playerFundBeforePurchasingSite
				- playerFundsAfterPurchasingSite,//
				Site.SITE_PRICE);
	}

	@Test
	public void testThatActionIsNotAvailableAfterDoingTheAction() {
		selectBigWorker();
		action.doAction();
		assertFalse(action.available().get());
	}

	@Test
	public void testPlayerChangesAfterActionIsComplete() {
		selectBigWorker();
		Player playerBeforeAction = game.currentPlayer().get();
		action.doAction();
		Player playerAfterAction = game.currentPlayer().get();
		assertNotEquals(playerBeforeAction, playerAfterAction);
	}

	@Test
	public void testWorkerIsNotSelectedAfterAction() {
		Worker worker = game.currentPlayer().get().bigWorker();
		worker.select();
		action.doAction();
		assertFalse(worker.isSelected());
	}
	
	private void selectBigWorker(){
		game.currentPlayer().get().bigWorker().select();
	}
	
	@Test
	public void testActionIsNotAvaliableWhenPlayerFundsIsNine(){
		setMarshFunds(9);
		selectBigWorker();
		assertFalse(action.available().get());
	}
	
	@Test
	public void testActionIsNotAvaliableWhenPlayerFundsIsTen(){
		setMarshFunds(10);
		selectBigWorker();
		assertTrue(action.available().get());
	}
	
	private void setMarshFunds(int i) {
		Game.currentGame().marsh.setFunds(Funds.valueOf(i));
	}
}
