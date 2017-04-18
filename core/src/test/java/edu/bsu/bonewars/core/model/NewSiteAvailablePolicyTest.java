package edu.bsu.bonewars.core.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class NewSiteAvailablePolicyTest {

	@Test
	public void testNewSiteAddedAtEndOfRound() {
		Game game = Game.create().setDestinyPolicy(NewSiteAvailableDestiny.instance());
		game.endRound();
		int numberOfAvailableSites = game.sites().size();
		assertEquals(Game.NUMBER_OF_STARTING_SITES + 1,
				numberOfAvailableSites);
	}
	
}
