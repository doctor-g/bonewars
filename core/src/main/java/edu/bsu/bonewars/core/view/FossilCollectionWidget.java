package edu.bsu.bonewars.core.view;

import static com.google.common.base.Preconditions.checkNotNull;
import static playn.core.PlayN.graphics;

import java.util.Map;

import playn.core.GroupLayer;
import playn.core.Layer;
import pythagoras.f.Point;
import pythagoras.i.Dimension;
import pythagoras.i.IDimension;
import tripleplay.util.Layers;

import com.google.common.collect.ImmutableMap;

import edu.bsu.bonewars.core.model.Fossil;
import edu.bsu.bonewars.core.model.Fossil.Type;
import edu.bsu.bonewars.core.model.FossilStack;
import edu.bsu.bonewars.core.model.Player;

public class FossilCollectionWidget {

	private final GroupLayer.Clipped layer;

	public static FossilCollectionWidget forPlayer(Player p) {
		return new FossilCollectionWidget(p);
	}

	private Player player;
	private static final IDimension SIZE = new Dimension(394, 440);
	private static final int FOSSIL_STACK_SPACING = 3;
	private static final Map<Fossil.Type, Point> FOSSIL_LOCATIONS;
	static {
		final int x = 40;
		ImmutableMap.Builder<Fossil.Type, Point> builder = ImmutableMap
				.builder();
		Fossil.Type[] types = Fossil.Type.values();
		builder.put(types[0], new Point(x, 0));
		for (int i = 1; i < types.length; i++) {
			float y = i
					* (FossilStackWidget.SIZE.height() + FOSSIL_STACK_SPACING);
			builder.put(types[i], new Point(x, y));
		}
		FOSSIL_LOCATIONS = builder.build();
	}

	private FossilCollectionWidget(Player p) {
		this.player = checkNotNull(p);
		this.layer = graphics().createGroupLayer(SIZE.width(), SIZE.height());
		for (Fossil.Type type : Fossil.Type.values()) {
			addFossilStack(player.fossilStack(type));
		}
	}

	private void addFossilStack(FossilStack fossilStack) {
		FossilStackWidget widget = FossilStackWidget.createFor(fossilStack);
		Point loc = FOSSIL_LOCATIONS.get(fossilStack.type());
		widget.layer().setTranslation(loc.x, loc.y);
		layer.add(widget.layer());
	}

	public Layer.HasSize layer() {
		return layer;
	}

	public Point pointFor(Type type) {
		Point inParentCoords = Layers.transform(FOSSIL_LOCATIONS.get(type),
				layer, layer.parent());
		return inParentCoords;
	}

}
