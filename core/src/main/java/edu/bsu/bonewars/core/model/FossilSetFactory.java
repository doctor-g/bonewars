package edu.bsu.bonewars.core.model;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

import edu.bsu.bonewars.core.model.Fossil.Quality;
import edu.bsu.bonewars.core.model.Fossil.Type;

public final class FossilSetFactory {

	private static final FossilSetFactory SINGLETON = new FossilSetFactory();

	public static FossilSetFactory instance() {
		return SINGLETON;
	}

	private static final Map<Quality, Integer> FOSSIL_QUALITY_DISTRIBUTION = ImmutableMap
			.of(Quality.HIGH, 1,//
					Quality.MEDIUM, 1, //
					Quality.LOW, 2,//
					Quality.VERY_LOW, 4);

	public Set<Fossil> createSetOf(Type type) {
		Set<Fossil> set = Sets.newHashSet();
		for (Quality quality : FOSSIL_QUALITY_DISTRIBUTION.keySet()) {
			for (int i = FOSSIL_QUALITY_DISTRIBUTION.get(quality); i > 0; i--) {
				Fossil fossil = Fossil.createWithQuality(quality).andWithType(
						type);
				set.add(fossil);
			}
		}
		return set;
	}

}
