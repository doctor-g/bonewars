package edu.bsu.bonewars.core.view;

import static com.google.common.base.Preconditions.checkState;
import static playn.core.PlayN.graphics;

import java.util.List;
import java.util.Map;
import java.util.Random;

import playn.core.GroupLayer;
import playn.core.Layer;
import pythagoras.f.Point;
import pythagoras.i.Dimension;
import pythagoras.i.IDimension;
import react.Slot;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import edu.bsu.bonewars.core.PlayingScreen;
import edu.bsu.bonewars.core.model.Game;
import edu.bsu.bonewars.core.model.Site;

public final class MapPanel {

	private static final IDimension SIZE = new Dimension(420, 450);
	private List<Point> siteLocations = Lists.newArrayList();

	public static MapPanel create() {
		return new MapPanel();
	}

	private final GroupLayer.Clipped layer;
	private Game game = Game.currentGame();
	private final Map<Site, SiteWidget> siteMapping = Maps.newHashMap();
	private boolean animationsEnabled = false;

	private MapPanel() {
		this.layer = graphics().createGroupLayer(SIZE.width(), SIZE.height());
		initSiteLocations();
		for (Site site : game.sites()) {
			placeSite(site);
		}
		game.onSiteAdded().connect(new Slot<Site>() {
			@Override
			public void onEmit(Site site) {
				placeSite(site);
			}
		});
	}

	private void placeSite(Site s) {
		try {
			addSiteWidgetFor(s, getPointFromSiteLocations());
		} catch (IndexOutOfBoundsException e) {
			throw new IllegalStateException(
					"I can currently only handle this many sites. Fix my siteLocations list.");
		}
	}

	private Point getPointFromSiteLocations() {
		checkState(!siteLocations.isEmpty());
		return siteLocations
				.remove((int) ((new Random().nextFloat()) * siteLocations
						.size()));
	}

	private void addSiteWidgetFor(Site site, Point point) {
		SiteWidget siteWidget = SiteWidget.createFor(site);
		layer.addAt(siteWidget.layer(), point.x, point.y);
		siteWidget.layer().setDepth(UiDepth.SITE.value);
		siteMapping.put(site, siteWidget);

		if (animationsEnabled) {
			PlayingScreen.screen().anim//
					.tweenScale(siteWidget.layer())//
					.from(0f)//
					.to(1.5f)//
					.in(500f)//
					.easeIn()//
					.then()//
					.tweenScale(siteWidget.layer())//
					.to(1f)//
					.in(250f)//
					.easeIn();
		}
	}

	private void initSiteLocations() {
		siteLocations.add(new Point(90, 135));
		siteLocations.add(new Point(242, 161));
		siteLocations.add(new Point(147, 210));
		siteLocations.add(new Point(48, 277));
		siteLocations.add(new Point(191, 280));
		siteLocations.add(new Point(310, 292));
		siteLocations.add(new Point(293, 354));
		siteLocations.add(new Point(183, 381));
	}

	public MapPanel enableAnimation() {
		animationsEnabled = true;
		return this;
	}

	public Layer.HasSize layer() {
		return layer;
	}

	public SiteWidget widgetFor(Site site) {
		return siteMapping.get(site);
	}
}
