package edu.bsu.bonewars.core.view;

import static com.google.common.base.Preconditions.checkNotNull;
import edu.bsu.bonewars.core.model.Game;
import react.Value;
import react.ValueView.Listener;

public final class AudioSystem {

	private static final AudioSystem SINGLETON = new AudioSystem();

	public static AudioSystem instance() {
		return SINGLETON;
	}

	public final Value<Boolean> sfxMute = Value.create(false);
	public final Value<Boolean> musicMute = Value.create(false);

	private GameMusic currentTrack = GameMusic.THEME;

	private AudioSystem() {
		musicMute.connect(new Listener<Boolean>() {
			@Override
			public void onChange(Boolean muted, Boolean oldValue) {
				if (muted) {
					currentTrack.sound.stop();
				} else {
					currentTrack.sound.play();
				}
			}
		});
	}

	public void play(GameMusic gameMusic) {
		if (currentTrack.sound.isPlaying()) {
			currentTrack.sound.stop();
		}
		this.currentTrack = checkNotNull(gameMusic);
		if (!musicMute.get())
			this.currentTrack.sound.play();
	}
	
	public void playTheme() {
		if (Game.currentGame().round().get() < 5) {
			play(GameMusic.THEME);
		}
		else {
			play(GameMusic.SECONDTHEME);
		}
	}
	
	public void play(GameSfx sfx) {
		if (!sfxMute.get())
			sfx.clip.play();
	}

}
