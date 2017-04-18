package edu.bsu.bonewars.core;

import static com.google.common.base.Preconditions.checkNotNull;
import static playn.core.PlayN.graphics;
import static playn.core.PlayN.log;
import playn.core.AssetWatcher.Listener;
import playn.core.Layer;
import pythagoras.f.Dimension;
import tripleplay.game.Screen;
import tripleplay.game.ScreenStack;
import tripleplay.game.UIAnimScreen;
import tripleplay.ui.Label;
import tripleplay.ui.Root;
import tripleplay.ui.Shim;
import tripleplay.ui.Style;
import tripleplay.ui.layout.AxisLayout;
import tripleplay.util.Colors;
import edu.bsu.bonewars.core.BoneWarsGame.StarterConfiguration;
import edu.bsu.bonewars.core.util.ProgressAssetWatcher;
import edu.bsu.bonewars.core.view.BoneWarsStylesheetFactory;
import edu.bsu.bonewars.core.view.GameFont;
import edu.bsu.bonewars.core.view.GameImage;
import edu.bsu.bonewars.core.view.GameMusic;
import edu.bsu.bonewars.core.view.GameSfx;
import edu.bsu.bonewars.core.view.LoadingProgressBar;

public class LoadingScreen extends UIAnimScreen {

	private final ScreenStack screenStack;
	private StarterConfiguration conf;

	public LoadingScreen(ScreenStack screenStack) {
		this.screenStack = checkNotNull(screenStack);
	}

	@Override
	public void wasAdded() {
		super.wasAdded();

		initBackgroundImage();

		Root root = iface.createRoot(AxisLayout.vertical(),
				BoneWarsStylesheetFactory.sheet(), layer)//
				.setSize(graphics().width(), graphics().height());

		root.add(new Shim(0, 470),//
				new Label("Who will earn the most fame in eight rounds?")//
						.addStyles(Style.COLOR.is(Colors.BLACK),//
								Style.HALIGN.center,//
								Style.FONT.is(GameFont.REGULAR.atSize(24f))));

	}

	private void initBackgroundImage() {
		Layer bg = graphics().createImageLayer(
				GameImage.LOADING_BACKGROUND.image);
		layer.add(bg);
	}

	public Screen withTransitionConfiguration(StarterConfiguration conf) {
		this.conf = checkNotNull(conf);
		return this;
	}

	@Override
	public void wasShown() {
		initializeAssetWatcher();
	}

	private void initializeAssetWatcher() {
		ProgressAssetWatcher watcher = new ProgressAssetWatcher(new Listener() {
			@Override
			public void error(Throwable e) {
				log().error(e.getMessage());
			}

			@Override
			public void done() {
				startGame();
			}

			private void startGame() {
				final PlayingScreen playingScreen = PlayingScreen
						.create(screenStack);
				conf.startGame(LoadingScreen.this, playingScreen, screenStack);
			}
		});
		for (GameImage image : GameImage.values()) {
			watcher.add(image.image);
		}

		for (GameSfx s : GameSfx.values()) {
			watcher.add(s.clip.asSound());
		}

		for (GameMusic song : GameMusic.values()) {
			watcher.add(song.sound);
		}

		watcher.start();

		final Dimension BAR_SIZE = new Dimension(graphics().width() * 0.55f, 10f);
		layer.addAt(
				new LoadingProgressBar(watcher, BAR_SIZE).layer(),
				graphics().width() / 2 - BAR_SIZE.width / 2, //
				graphics().height() - 45);
	}

}
