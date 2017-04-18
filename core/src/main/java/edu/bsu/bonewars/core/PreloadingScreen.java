package edu.bsu.bonewars.core;

import static com.google.common.base.Preconditions.checkNotNull;
import playn.core.AssetWatcher;
import playn.core.AssetWatcher.Listener;
import tripleplay.game.Screen;
import tripleplay.game.ScreenStack;
import tripleplay.game.trans.FadeTransition;
import edu.bsu.bonewars.core.view.GameImage;

public class PreloadingScreen extends Screen {

	private final ScreenStack screenStack;
	private final Screen next;

	public PreloadingScreen(ScreenStack screenStack, Screen next) {
		this.screenStack = checkNotNull(screenStack);
		this.next = checkNotNull(next);
	}

	@Override
	public void wasShown() {
		AssetWatcher watcher = new AssetWatcher(new Listener() {
			@Override
			public void error(Throwable e) {
				throw new IllegalStateException(e);
			}

			@Override
			public void done() {
				screenStack.push(next, new FadeTransition(screenStack));
			}
		});
		watcher.add(GameImage.LOADING_BACKGROUND.image);
		watcher.start();
	}
}
