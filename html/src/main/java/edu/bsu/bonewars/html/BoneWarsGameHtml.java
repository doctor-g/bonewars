package edu.bsu.bonewars.html;

import playn.core.PlayN;
import playn.html.HtmlGame;
import playn.html.HtmlPlatform;
import tripleplay.game.ScreenStack;
import edu.bsu.bonewars.core.BoneWarsGame;
import edu.bsu.bonewars.core.LoadingScreen;
import edu.bsu.bonewars.core.PlayingScreen;

public class BoneWarsGameHtml extends HtmlGame {

	@Override
	public void start() {
		configureHTMLPlatform();
		PlayN.run(new BoneWarsGame(new BoneWarsGame.StarterConfiguration() {

			@Override
			public void startGame(final LoadingScreen loadingScreen,
					final PlayingScreen playingScreen,
					final ScreenStack screenStack) {
				loadingScreen.anim.delay(1000f).then().action(new Runnable() {
					@Override
					public void run() {
						screenStack.replace(playingScreen,
								screenStack.pageTurn());
					}
				});
			}
		}));
	}

	private void configureHTMLPlatform() {
		HtmlPlatform platform = HtmlPlatform.register();
		platform.assets().setPathPrefix("bonewars/");
	}
}
