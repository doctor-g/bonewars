package edu.bsu.bonewars.core.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import edu.bsu.bonewars.core.model.Fossil.Quality;
import edu.bsu.bonewars.core.model.Fossil.Type;

public class SiteFactoryTest {

	private SiteFactory siteFactory;

	@Before
	public void setUp() {
		this.siteFactory = SiteFactory.create();
	}

	@Test
	public void testSiteHasRightNumberOfFossils() {
		Site site = siteFactory.next();
		assertEquals(6, site.fossils().size());
	}

	@Test
	public void testNext_repeatCallYieldsDifferentResult() {
		Site first = siteFactory.next();
		Site second = siteFactory.next();
		assertFalse(first.equals(second));
	}

	@Test
	public void testSequenceOfFossilTypesIsNotAlwaysTheSame() {
		// In the implementation, the sequence specified as ABBCCCC, for
		// programmer convenience. We want to ensure that sequence does not
		// actually show up in the game. We will run the test many times to
		// protect against probability that it *could* happen with this sequence
		// by chance. That might not actually be possible, based on the theories
		// of pseudorandom numbers, but it's not worth the analysis.
		boolean allInSameSequence = true;
		for (int i = 0; i < 100; i++) {
			allInSameSequence = allInSameSequence
					&& isEverySequenceInMagicOrder();
		}
		assertFalse(allInSameSequence);
	}

	private boolean isEverySequenceInMagicOrder() {
		SiteFactory factory = SiteFactory.create();
		while (factory.hasNext()) {
			Site site = factory.next();
			list = site.fossils();
			if (!isListInMagicSequence()) {
				return false;
			}
		}
		return true;
	}

	private List<Fossil> list;

	private boolean isListInMagicSequence() {
		return same(1, 2)//
				&& same(3, 4, 5) //
				&& different(0, 1)//
				&& different(1, 3);

	}

	private boolean same(int... indices) {
		for (int i = 1; i < indices.length; i++) {
			if (different(indices[0], indices[i]))
				return false;
		}
		return true;
	}

	private boolean different(int i, int j) {
		return !list.get(i).type().equals(list.get(j).type());
	}

	@Test
	public void testIsMagicSequence_metaTest_true() {
		list = ImmutableList.of(a(), b(), b(), c(), c(), c());
		assertTrue(isListInMagicSequence());
	}

	private Fossil a() {
		return Fossil.createWithQuality(Quality.LOW).andWithType(Type.A);
	}

	private Fossil b() {
		return Fossil.createWithQuality(Quality.LOW).andWithType(Type.B);
	}

	private Fossil c() {
		return Fossil.createWithQuality(Quality.LOW).andWithType(Type.C);
	}

	@Test
	public void testIsMagicSequence_metaTest_false() {
		list = ImmutableList.of(a(), a(), b(), c(), c(), c());
		assertFalse(isListInMagicSequence());
	}

	@Test
	public void testSame_metaTest_true() {
		list = ImmutableList.of(a(), a());
		assertTrue(same(0, 1));
	}

	@Test
	public void testSame_metaTest_false() {
		list = ImmutableList.of(a(), b());
		assertFalse(same(0, 1));
	}

	@Test
	public void testDifferent_metaTest_true() {
		list = ImmutableList.of(a(), b());
		assertTrue(different(0, 1));
	}

	@Test
	public void testDifferent_metaTest_false() {
		list = ImmutableList.of(a(), a());
		assertFalse(different(0, 1));
	}
}
