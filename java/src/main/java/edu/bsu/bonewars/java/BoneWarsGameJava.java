package edu.bsu.bonewars.java;

import playn.core.PlayN;
import playn.java.JavaPlatform;
import tripleplay.game.ScreenStack;
import edu.bsu.bonewars.core.BoneWarsGame;
import edu.bsu.bonewars.core.LoadingScreen;
import edu.bsu.bonewars.core.PlayingScreen;
import edu.bsu.bonewars.core.view.GameFont;

public class BoneWarsGameJava {

	private static JavaPlatform platform;

	public static void main(String[] args) {
		configureJavaPlatform();
		registerFonts();
		startGame();
	}

	private static void configureJavaPlatform() {
		JavaPlatform.Config config = new JavaPlatform.Config();
		config.width = 800;
		config.height = 600;
		platform = JavaPlatform.register(config);
	}

	private static void startGame() {
		BoneWarsGame game = new BoneWarsGame(
				new BoneWarsGame.StarterConfiguration() {
					@Override
					public void startGame(LoadingScreen loadingScreen,
							PlayingScreen playingScreen, ScreenStack screenStack) {
						screenStack.push(playingScreen);
					}
				});
		PlayN.run(game);
	}

	private static void registerFonts() {
		for (GameFont font : GameFont.values()) {
			platform.graphics().registerFont(font.name, font.path);
		}
	}
}
