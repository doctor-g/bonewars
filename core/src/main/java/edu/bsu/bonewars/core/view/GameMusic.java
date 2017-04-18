package edu.bsu.bonewars.core.view;

import static playn.core.PlayN.assets;
import playn.core.Sound;

public enum GameMusic {

	THEME("music/OpeningTheme"), //
	VICTORY("music/victory"), //
	SECONDTHEME("music/SecondTheme"), //
	INDIAN_ATTACK_STORY_CARD("music/BoneWars_Indian_Attack"), //
	SILVER_MINE_STORY_CARD("music/BoneWars_Pickaxes_and_Stones"), //
	FOSSIL_INFLUX_STORY_CARD("music/BoneWars_Researching_Bones1"), FOSSIL_HUNTER_THEME(
			"music/BoneWars_Marsh_Theme"); //

	public final Sound sound;

	private GameMusic(String path) {
		this.sound = assets().getMusic(path);
		this.sound.setVolume(0.65f);
		this.sound.setLooping(true);
	}

	public void play() {
		AudioSystem.instance().play(this);
	}
}
