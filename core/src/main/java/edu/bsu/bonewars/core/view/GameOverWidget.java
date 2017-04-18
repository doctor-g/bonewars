package edu.bsu.bonewars.core.view;

import static com.google.common.base.Preconditions.checkNotNull;
import static playn.core.PlayN.graphics;
import playn.core.GroupLayer;
import playn.core.ImageLayer;
import playn.core.Layer;
import pythagoras.i.Dimension;
import pythagoras.i.IDimension;
import react.SignalView;
import react.Slot;
import react.UnitSignal;
import tripleplay.ui.Button;
import tripleplay.ui.Root;
import tripleplay.ui.Style;
import tripleplay.ui.layout.BorderLayout;
import edu.bsu.bonewars.core.PlayingScreen;
import edu.bsu.bonewars.core.model.Player;

public final class GameOverWidget {
	private final GroupLayer.Clipped layer;

	public static GameOverWidget create(Player winner) {
		return new GameOverWidget(winner);
	}

	private static final IDimension SIZE = new Dimension(415, 441);
	private final Player winner;
	private final UnitSignal onRestartGame = new UnitSignal();

	private GameOverWidget(Player winner) {
		this.winner = checkNotNull(winner);
		this.layer = graphics().createGroupLayer(SIZE.width(), SIZE.height());
		AudioSystem.instance().play(GameMusic.VICTORY);
		initUI();
	}

	private void initUI() {
		layer.add(getBackgroundLayer());
		Root root = PlayingScreen.screen().iface.createRoot(new BorderLayout(),//
				BoneWarsStylesheetFactory.sheet(),//
				layer)//
				.setSize(SIZE.width(), SIZE.height());
		addRestartButton(root);
	}

	private void addRestartButton(Root root) {
		Button restart = createRestartButton()//
				.setConstraint(BorderLayout.SOUTH.unstretched());
		root.add(restart);

	}

	private Button createRestartButton() {
		return new Button("Play Again!").setStyles(
				Style.FONT.is(AmbiguousPublishDialog.buttonFont),
				Style.BACKGROUND.is(AmbiguousPublishDialog.buttonBackground))
				.onClick(new Slot<Button>() {
					@Override
					public void onEmit(Button event) {
						onRestartGame.emit();
					}
				});
	}

	private ImageLayer getBackgroundLayer() {
		ImageLayer ownershipColorBg;
		ownershipColorBg = winner.isMarsh() ? graphics().createImageLayer(
				GameImage.MARSH_WIN.image) : graphics().createImageLayer(
				GameImage.COPE_WIN.image);
		return ownershipColorBg;
	}

	public Layer.HasSize layer() {
		return layer;
	}

	public SignalView<Void> onRestartGame() {
		return onRestartGame;
	}

}
