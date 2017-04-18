package edu.bsu.bonewars.core.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

public class SiteTest {

	private Site aSite;

	@Before
	public void setup() {
		List<Fossil> fossilsForSite = Lists.newArrayList();
		for (int i = 0; i < Game.NUMBER_OF_FOSSILS_PER_SITE; i++) {
			fossilsForSite.add(mock(Fossil.class));
		}

		aSite = createASite();
		Game.create();
	}

	private Site createASite() {
		List<Fossil> fossilsForSite = Lists.newArrayList();
		for (int i = 0; i < Game.NUMBER_OF_FOSSILS_PER_SITE; i++) {
			fossilsForSite.add(mock(Fossil.class));
		}
		return Site.createWithFossils(fossilsForSite);
	}

	@Test
	public void testASiteInitializedWithUnassignedOwner() {
		assertFalse(aSite.hasOwner().get());
	}

	@Test
	public void testEquals_identity() {
		assertTrue(aSite.equals(aSite));
	}

	@Test
	public void testEquals_differentObjectsAreUnequal() {
		Site another = createASite();
		assertFalse(another.equals(aSite));
	}

	@Test
	public void testEquals_differentObjectsAreUnequal_associative() {
		Site another = createASite();
		assertFalse(aSite.equals(another));
	}
}
