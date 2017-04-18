package edu.bsu.bonewars.core.model;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import tripleplay.util.Randoms;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import edu.bsu.bonewars.core.model.Fossil.Type;

public final class SiteFactory implements Iterator<Site> {

	public static SiteFactory create() {
		return new SiteFactory();
	}

	private static final Randoms randoms = Randoms.with(new Random());

	private static final int[][] FOSSIL_TYPE_DISTRIBUTION = {
			{ 0, 1, 1, 2, 2, 2 },//
			{ 3, 4, 4, 5, 5, 5 },//
			{ 6, 7, 7, 0, 0, 0 },//
			{ 1, 2, 2, 3, 3, 3 },//
			{ 4, 5, 5, 6, 6, 6 },//
			{ 7, 0, 0, 1, 1, 1 },//
			{ 2, 3, 3, 4, 4, 4 },//
			{ 5, 6, 6, 7, 7, 7 } //
	};

	private final Map<Integer, Type> typeMap;

	private final Map<Type, List<Fossil>> fossils;

	private final List<Site> sites;

	private SiteFactory() {
		this.fossils = createFossils();
		this.typeMap = randomlyMapTypeToInteger();
		this.sites = createSites();
	}

	private ImmutableMap<Type, List<Fossil>> createFossils() {
		FossilSetFactory fossilSetFactory = FossilSetFactory.instance();
		ImmutableMap.Builder<Type, List<Fossil>> builder = ImmutableMap
				.builder();
		for (Type type : Type.values()) {
			List<Fossil> fossils = Lists.newArrayList(fossilSetFactory
					.createSetOf(type));
			builder.put(type, fossils);
		}
		return builder.build();
	}

	private Map<Integer, Type> randomlyMapTypeToInteger() {
		List<Integer> keys = Lists.newArrayList(0, 1, 2, 3, 4, 5, 6, 7);
		Map<Integer, Type> map = Maps.newHashMap();
		Type[] types = Type.values();
		for (int i = 0; !keys.isEmpty(); i++) {
			Integer key = removeRandomFrom(keys);
			map.put(key, types[i]);
		}
		return map;
	}

	private List<Site> createSites() {
		List<Site> sites = Lists.newArrayList();
		for (int i = 0; i < FOSSIL_TYPE_DISTRIBUTION.length; i++) {
			int[] distribution = FOSSIL_TYPE_DISTRIBUTION[i];
			Site site = createSiteFrom(distribution);
			sites.add(site);
		}
		return sites;
	}

	private Site createSiteFrom(int[] distribution) {
		List<Fossil> result = Lists.newArrayList();
		for (int i = 0; i < distribution.length; i++) {
			Type type = typeMap.get(distribution[i]);
			List<Fossil> fossilsOfType = fossils.get(type);
			Fossil fossil = removeRandomFrom(fossilsOfType);
			result.add(fossil);
		}
		randoms.shuffle(result);
		return Site.createWithFossils(result);
	}

	private static <T> T removeRandomFrom(List<T> list) {
		return list.remove(randoms.getInt(list.size()));
	}

	@Override
	public boolean hasNext() {
		return !sites.isEmpty();
	}

	@Override
	public Site next() {
		if (sites.isEmpty())
			throw new OutOfSiteException();
		return sites.remove(0);
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	public static final class OutOfSiteException extends RuntimeException {
		private static final long serialVersionUID = 5080373481567526485L;
	}
}
