package edu.bsu.bonewars.core;

import static com.google.common.base.Preconditions.checkNotNull;
import playn.core.Game;
import playn.core.util.Clock;
import tripleplay.anim.Animator;
import tripleplay.game.ScreenStack;
import tripleplay.sound.SoundBoard;

public class BoneWarsGame extends Game.Default {

	public interface StarterConfiguration {
		public void startGame(LoadingScreen loadingScreen,
				PlayingScreen playingScreen, ScreenStack screenStack);
	}

	private static final int UPDATE_RATE = 33;

	private final Clock.Source clock = new Clock.Source(UPDATE_RATE);
	private final ScreenStack screenStack = new ScreenStack();
	private static SoundBoard soundBoard = new SoundBoard();
	private Animator anim;
	private final StarterConfiguration conf;

	public BoneWarsGame(StarterConfiguration conf) {
		super(UPDATE_RATE);
		this.conf = checkNotNull(conf);
	}

	@Override
	public void init() {
		anim = new Animator();
		screenStack.push(new PreloadingScreen(screenStack, //
				new LoadingScreen(screenStack)
						.withTransitionConfiguration(conf)));
		;
	}

	public static SoundBoard getSoundBoard() {
		return soundBoard;
	}

	@Override
	public void update(int deltaMS) {
		clock.update(deltaMS);
		screenStack.update(deltaMS);
		soundBoard.update(deltaMS);
	}

	@Override
	public void paint(float alpha) {
		clock.paint(alpha);
		screenStack.paint(clock);
		anim.paint(clock);
	}
}
