package edu.bsu.bonewars.core.view;

import static playn.core.PlayN.graphics;
import playn.core.GroupLayer;
import playn.core.ImageLayer;
import playn.core.Layer;
import react.ValueView.Listener;
import tripleplay.ui.Label;
import tripleplay.ui.Root;
import tripleplay.ui.layout.AxisLayout;
import edu.bsu.bonewars.core.PlayingScreen;
import edu.bsu.bonewars.core.model.Game;

public class RoundCounterWidget {

	private final GroupLayer layer;
	private final ImageLayer background;
	private final Label label;

	public static RoundCounterWidget create() {
		return new RoundCounterWidget();
	}

	private RoundCounterWidget() {
		layer = graphics().createGroupLayer();
		background = graphics().createImageLayer(GameImage.ROUND_PLACARD.image);
		layer.add(background);

		Root root = PlayingScreen.screen().iface.createRoot(
				AxisLayout.horizontal(), BoneWarsStylesheetFactory.sheet(),
				layer)//
				.setSize(background.width(), background.height());

		root.add(label = new Label());
		updateText();

		Game.currentGame().round().connect(new Listener<Integer>() {
			@Override
			public void onChange(Integer value, Integer oldValue) {
				updateText();
			}
		});
	}

	private void updateText() {
		label.text.update("Round "
				+ Game.currentGame().round().get().toString());
	}

	public Layer layer() {
		return layer;
	}
}
