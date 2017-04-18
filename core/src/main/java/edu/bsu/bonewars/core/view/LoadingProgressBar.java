package edu.bsu.bonewars.core.view;

import static com.google.common.base.Preconditions.checkNotNull;
import static playn.core.PlayN.graphics;
import playn.core.Canvas;
import playn.core.CanvasImage;
import playn.core.ImageLayer;
import playn.core.Layer;
import pythagoras.f.IDimension;
import react.ValueView.Listener;
import tripleplay.util.Colors;
import edu.bsu.bonewars.core.util.ProgressAssetWatcher;

public final class LoadingProgressBar {

	private static final int BACKGROUND_COLOR = Palette.EARTHY_BROWN;
	private static final int FILL_COLOR = Colors.WHITE;
	private static final int BORDER = 1;

	private final ImageLayer layer;
	private final Canvas canvas;
	private final ProgressAssetWatcher watcher;

	public LoadingProgressBar(ProgressAssetWatcher watcher, IDimension size) {
		this.watcher = checkNotNull(watcher);
		CanvasImage image = graphics().createImage(size.width(), size.height());
		layer = graphics().createImageLayer(image);
		canvas = image.canvas();
		repaintCanvas();

		watcher.loaded().connect(new Listener<Integer>() {
			@Override
			public void onChange(Integer value, Integer oldValue) {
				repaintCanvas();
			}
		});
	}

	private void repaintCanvas() {
		float percent = (float) watcher.loaded().get().intValue()
				/ watcher.total();
		canvas.setFillColor(BACKGROUND_COLOR);
		canvas.fillRect(0, //
				0, //
				canvas.width(),//
				canvas.height());
		canvas.setFillColor(FILL_COLOR);
		canvas.fillRect(BORDER, BORDER,//
				(canvas.width() - (BORDER * 2)) * percent, //
				canvas.height() - (BORDER * 2));
	}

	public Layer.HasSize layer() {
		return layer;
	}
}
