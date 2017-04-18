package edu.bsu.bonewars.core.view;

import static playn.core.PlayN.graphics;
import playn.core.GroupLayer;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.Layer;
import edu.bsu.bonewars.core.model.Site;

public class AcquireSiteActionFlag {

	private static final Image bgImage = GameImage.ACQUIRE_FLAG.image;

	public static AcquireSiteActionFlag createFor(Site site) {
		return new AcquireSiteActionFlag(site);
	}

	private GroupLayer.Clipped layer;

	private AcquireSiteActionFlag(Site site) {
		layer = graphics().createGroupLayer(bgImage.width(), bgImage.height());
		ImageLayer bg = graphics().createImageLayer(
				GameImage.ACQUIRE_FLAG.image);
		layer.add(bg);
	}

	public Layer.HasSize layer() {
		return layer;
	}
}
