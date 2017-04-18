package edu.bsu.bonewars.core.view;

import static com.google.common.base.Preconditions.checkNotNull;
import static playn.core.PlayN.graphics;
import playn.core.GroupLayer;
import playn.core.Layer;
import pythagoras.f.Point;
import pythagoras.i.Dimension;
import pythagoras.i.IDimension;

import com.google.common.collect.ImmutableMap;

import edu.bsu.bonewars.core.model.Fossil;
import edu.bsu.bonewars.core.model.Fossil.Type;
import edu.bsu.bonewars.core.model.PublishedFossilsArea;

public class PublishedFossilsAreaWidget {
	private final GroupLayer.Clipped layer;

	public static PublishedFossilsAreaWidget create(
			PublishedFossilsArea publishedFossilsArea) {
		checkNotNull(publishedFossilsArea);
		return new PublishedFossilsAreaWidget(publishedFossilsArea);
	}

	private static final IDimension SIZE = new Dimension(791, 75);
	private static final int PUBLISH_TYPE_SPACING = 21;
	private static final int LEFT_BOUNDS = 22;
	private static final int TOP_BOUNDS = 10;
	private static final ImmutableMap<Fossil.Type, Point> TYPE_LOCATIONS;
	static {
		final int y = TOP_BOUNDS;
		ImmutableMap.Builder<Fossil.Type, Point> builder = ImmutableMap
				.builder();
		Fossil.Type[] types = Fossil.Type.values();
		builder.put(types[0], new Point(LEFT_BOUNDS, y));
		for (int i = 1; i < types.length; i++) {
			float x = LEFT_BOUNDS + i
					* (PublishedFossilWidget.SIZE.width() + PUBLISH_TYPE_SPACING);
			builder.put(types[i], new Point(x, y));
		}
		TYPE_LOCATIONS = builder.build();
	}

	private PublishedFossilsAreaWidget(PublishedFossilsArea publishedFossilsArea) {
		this.layer = graphics().createGroupLayer(SIZE.width(), SIZE.height());
	}

	public Layer.HasSize layer() {
		return layer;
	}

	public static Point pointFor(Type type) {
		return TYPE_LOCATIONS.get(type);
	}

	public void add(PublishedFossilWidget widget) {
		layer.add(widget.layer());
		Point pos = pointFor(widget.fossil.type());
		widget.layer().setTranslation(pos.x, pos.y);
	}
}
