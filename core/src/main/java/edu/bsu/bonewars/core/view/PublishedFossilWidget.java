package edu.bsu.bonewars.core.view;

import static com.google.common.base.Preconditions.checkNotNull;
import static playn.core.PlayN.graphics;
import playn.core.GroupLayer;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.Layer;
import pythagoras.i.Dimension;
import pythagoras.i.IDimension;
import edu.bsu.bonewars.core.model.Fossil;

public class PublishedFossilWidget {
	private final GroupLayer.Clipped layer;

	public static final IDimension SIZE = new Dimension(75, 50);

	public static PublishedFossilWidget create(Fossil publishedFossil) {
		checkNotNull(publishedFossil);
		return new PublishedFossilWidget(publishedFossil);
	}

	public final Fossil fossil;

	private PublishedFossilWidget(Fossil fossil) {
		this.fossil = checkNotNull(fossil);

		Image bgImage = GameImage.PUBLISH_FOSSIL_WIDGET_BACKGROUND.image;
		ImageLayer bgLayer = graphics().createImageLayer(bgImage);

		layer = graphics().createGroupLayer(bgImage.width(), bgImage.height());
		layer.add(bgLayer);

		ImageLayer tintLayer = ViewUtils.createSolidImage(SIZE, fossil.owner()
				.isCope() ? Palette.COPE_BLUE : Palette.MARSH_BROWN);
		tintLayer.setAlpha(0.3f);
		layer.add(tintLayer);

		Image fossilTypeImage = GameImage.getGameImageForFossilType(fossil
				.type());
		ImageLayer fossilTypeLayer = graphics().createImageLayer(
				fossilTypeImage);
		fossilTypeLayer.setTranslation(5, 5);
		layer.add(fossilTypeLayer);

		Image qualityImage = GameImage.completenessFor(fossil.quality());
		ImageLayer qualityImageLayer = graphics()
				.createImageLayer(qualityImage);
		qualityImageLayer.setTranslation(5 + fossilTypeLayer.width() + 5, 5);
		layer.add(qualityImageLayer);
	}

	public Layer.HasSize layer() {
		return layer;
	}
}
