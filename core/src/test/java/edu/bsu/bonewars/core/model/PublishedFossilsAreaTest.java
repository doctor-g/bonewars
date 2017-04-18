package edu.bsu.bonewars.core.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import react.Slot;
import edu.bsu.bonewars.core.model.Fossil.Quality;
import edu.bsu.bonewars.core.model.Fossil.Type;
import edu.bsu.bonewars.core.model.PublishedFossilsArea.TrumpEvent;

public class PublishedFossilsAreaTest {

	private PublishedFossilsArea area;
	private Player cope;
	private Player marsh;

	@Before
	public void setUp() {
		Game.create();
		cope = Player.createCope();
		marsh = Player.createMarsh();
		area = PublishedFossilsArea.create();
	}

	@Test
	public void testAfterPublishingThereIsOneFossil() {
		area.publish(Fossil.createWithQuality(Quality.HIGH).andWithType(Type.C));
		assertEquals(1, area.getPublishedFossilsOfType(Type.C).getAllFossils()
				.size());
	}

	@Test
	public void testFossilIsPublishable_SameTypeHasNotBeenPublished() {
		Fossil mediumQualityFossil = Fossil.createWithQuality(Quality.MEDIUM)
				.andWithType(Type.A);
		assertTrue(area.isPublishable(mediumQualityFossil));
	}

	@Test
	public void testFossilIsPublishable_LowerQualityIsPublished() {
		Fossil lowQualityFossil = Fossil.createWithQuality(Quality.MEDIUM)
				.andWithType(Type.A);
		Fossil mediumQualityFossil = Fossil.createWithQuality(Quality.MEDIUM)
				.andWithType(Type.A);
		area.publish(lowQualityFossil);
		assertTrue(area.isPublishable(mediumQualityFossil));
	}

	@Test
	public void testFossilIsPublishable_EqualQualityIsPublished() {
		Fossil firstMediumFossil = Fossil.createWithQuality(Quality.MEDIUM)
				.andWithType(Type.A);
		area.publish(firstMediumFossil);
		Fossil secondMediumFossil = Fossil.createWithQuality(Quality.MEDIUM)
				.andWithType(Type.A);
		assertTrue(area.isPublishable(secondMediumFossil));
	}

	@Test
	public void testFossilIsNotPublishable_HigherQualityIsPublished() {
		Fossil firstFossil = Fossil.createWithQuality(Quality.HIGH)
				.andWithType(Type.A);
		area.publish(firstFossil);
		Fossil secondFossil = Fossil.createWithQuality(Quality.MEDIUM)
				.andWithType(Type.A);
		assertFalse(area.isPublishable(secondFossil));
	}

	@Test
	public void testThatAfterPublishingSeveralFossilsOfTheSameTypeThatPublishedFossilsCanShowTheHighestFossilOfAGivenType() {
		Fossil fossil1 = Fossil.createWithQuality(Quality.LOW).andWithType(
				Type.A);
		Fossil fossil2 = Fossil.createWithQuality(Quality.MEDIUM).andWithType(
				Type.B);
		Fossil fossil3 = Fossil.createWithQuality(Quality.LOW).andWithType(
				Type.A);
		Fossil fossil4 = Fossil.createWithQuality(Quality.MEDIUM).andWithType(
				Type.A);
		Fossil fossil5 = Fossil.createWithQuality(Quality.HIGH).andWithType(
				Type.A);
		fossil1.setOwner(mock(Player.class));
		fossil2.setOwner(mock(Player.class));
		fossil3.setOwner(mock(Player.class));
		fossil4.setOwner(mock(Player.class));
		fossil5.setOwner(mock(Player.class));
		area.publish(fossil1);
		area.publish(fossil2);
		area.publish(fossil3);
		area.publish(fossil4);
		area.publish(fossil5);
		assertEquals(fossil5, area.getPublishedFossilsOfType(Type.A)
				.getBestFossil().get());
	}

	@Test
	public void testTrumpSignal() {
		Slot<TrumpEvent> mockSlot = publishTrump();
		verify(mockSlot).onEmit(Matchers.any(TrumpEvent.class));
	}

	private Slot<TrumpEvent> publishTrump() {
		Fossil fossil1 = Fossil.createWithQuality(Quality.MEDIUM).andWithType(
				Type.A);
		Fossil fossil2 = Fossil.createWithQuality(Quality.HIGH).andWithType(
				Type.A);
		fossil1.setOwner(cope);
		fossil2.setOwner(marsh);
		@SuppressWarnings("unchecked")
		Slot<TrumpEvent> mockSlot = mock(Slot.class);
		area.playerWasTrumpedSignal().connect(mockSlot);
		area.publish(fossil1);
		area.publish(fossil2);
		return mockSlot;
	}

	@Test
	public void testThatPublishedFossilsAreaCanReturnAListOfAllPublishedFossilsOfAGivenType() {
		Fossil fossil1 = Fossil.createWithQuality(Quality.VERY_LOW)
				.andWithType(Type.A);
		Fossil fossil2 = Fossil.createWithQuality(Quality.LOW).andWithType(
				Type.A);
		Fossil fossil3 = Fossil.createWithQuality(Quality.MEDIUM).andWithType(
				Type.A);
		Fossil fossil4 = Fossil.createWithQuality(Quality.HIGH).andWithType(
				Type.A);
		fossil1.setOwner(mock(Player.class));
		fossil2.setOwner(mock(Player.class));
		fossil3.setOwner(mock(Player.class));
		fossil4.setOwner(mock(Player.class));
		area.publish(fossil1);
		area.publish(fossil2);
		area.publish(fossil3);
		area.publish(fossil4);
		assertEquals(4, area.getPublishedFossilsOfType(Type.A).getAllFossils()
				.size());
	}
}
