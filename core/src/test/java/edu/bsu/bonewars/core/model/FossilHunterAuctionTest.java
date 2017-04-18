package edu.bsu.bonewars.core.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

import react.UnitSlot;

public class FossilHunterAuctionTest {

	private FossilHunterAuction auction;
	private Player marsh;
	private Player cope;

	@Before
	public void setUp() {
		Game.create();
		marsh = Game.currentGame().marsh;
		cope = Game.currentGame().cope;
		auction = FossilHunterAuction.create();
	}

	@Test
	public void testFossil_initial_notNull() {
		assertNotNull(auction.fossil);
	}

	@Test
	public void testHasBids_initial_noBids() {
		assertFalse(auction.hasBids());
	}

	@Test
	public void testHasBids_marshBid_true() {
		whenMarshBids();
		assertTrue(auction.hasBids());
	}

	private void whenMarshBids() {
		auction.bid(marsh);
	}

	@Test
	public void testHighBider_marshBidFirst_isMarsh() {
		whenMarshBids();
		assertEquals(marsh, auction.highBidder.get());
	}

	@Test
	public void testNextBid_initial_isPositive() {
		assertTrue(auction.nextBidAmount.get().asInt() > 0);
	}

	@Test
	public void testHighBidder_copeAfterMarsh_isCope() {
		whenMarshBids();
		whenCopeBids();
		assertEquals(cope, auction.highBidder.get());
	}

	private void whenCopeBids() {
		auction.bid(cope);
	}

	@Test(expected = IllegalStateException.class)
	public void testMarshBid_marshIsBroke_throwsException() {
		givenMarshIsBroke();
		whenMarshBids();
	}

	private void givenMarshIsBroke() {
		marsh.setFunds(Funds.valueOf(0));
	}

	@Test
	public void testCanBid_marshCan_true() {
		assertTrue(auction.canBid(marsh));
	}

	@Test
	public void testCanBid_marshIsBroke_false() {
		givenMarshIsBroke();
		assertFalse(auction.canBid(marsh));
	}

	@Test
	public void testOnBid_marshBids_notified() {
		UnitSlot slot = mock(UnitSlot.class);
		auction.onBid(slot);
		whenMarshBids();
		verify(slot).onEmit();
	}

	@Test
	public void testComplete_noBids_noFossils() {
		auction.complete();
		assertTrue(marsh.fossilStack(auction.fossil.type()).isEmpty()
				&& cope.fossilStack(auction.fossil.type()).isEmpty());
	}

	@Test
	public void testComplete_marshIsHighBidder_marshGetsFossil() {
		whenMarshBids();
		auction.complete();
		assertTrue(marsh.fossilStack(auction.fossil.type()).contains(
				auction.fossil));
	}

	@Test
	public void testComplete_marshIsHighBidder_fundsReducedByBidAmount() {
		Funds before = marsh.funds().get();
		whenMarshBids();
		auction.complete();
		assertEquals(auction.highBid.get(),
				before.subtract(marsh.funds().get()));
	}
}
