package edu.bsu.bonewars.core.view;

import static com.google.common.base.Preconditions.checkArgument;
import static playn.core.PlayN.graphics;
import playn.core.CanvasImage;
import playn.core.GroupLayer;
import playn.core.ImageLayer;
import playn.core.Layer;
import pythagoras.i.IDimension;
import tripleplay.util.Colors;

public class ViewUtils {

	public static ImageLayer createSolidImage(IDimension size, int color) {
		CanvasImage image = graphics().createImage(size.width(), size.height());
		image.canvas().setFillColor(color);
		image.canvas().fillRect(0, 0, image.width(), image.height());
		ImageLayer imageLayer = graphics().createImageLayer(image);
		return imageLayer;
	}

	// This method is designed for use in debugging the view, to make sure that
	// a layer's bounds are where a developer expects it to be.
	@SuppressWarnings("unused")
	private void addTranslucencyTo(GroupLayer.Clipped layer) {
		CanvasImage image = graphics().createImage(layer.width(),
				layer.height());
		image.canvas().setAlpha(0.5f);
		image.canvas().setFillColor(Colors.GRAY);
		image.canvas().fillRect(0, 0, image.width(), image.height());
		layer.add(graphics().createImageLayer(image));
	}

	public static void centerInScreen(Layer.HasSize child) {
		checkArgument(child.parent() != null, "Layer has no parent");
		child.setTx((graphics().width() - child.width()) / 2);
		child.setTy((graphics().height() - child.height()) / 2);
	}

}
