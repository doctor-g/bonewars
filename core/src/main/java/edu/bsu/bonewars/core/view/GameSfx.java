package edu.bsu.bonewars.core.view;

import java.util.Random;

import tripleplay.sound.Clip;
import tripleplay.util.Randoms;

import com.google.common.collect.ImmutableList;

import edu.bsu.bonewars.core.BoneWarsGame;

public enum GameSfx {

	ANALYZE("analyze"), //
	ACQUIRE("Acquire_A_Site"), //
	EXCAVATE("new_excavate"), //
	PUBLISH("Publish_SFX_Clapping"), //
	RAISE_FUNDS("raise_funds"), //
	LITTLE_WORKER("what_do_you_need"), //
	LITTLE_WORKER2("hi"), //
	LITTLE_WORKER3("need_help"), //
	LITTLE_WORKER4("what_do_you_have_there"), //
	LITTLE_WORKER5("how_are_you"), //
	LITTLE_WORKER6("worker_grunt"), //
	ADD_WORKER("ready_for_hire"), //
	MARSH_WORKER("marsh_worker"), //
	COPE_WORKER("cope_worker"), //
	ROUND_CHANGE("Round_Change_SFX"); //

	private static final String PREFIX = "sfx/";
	private static final ImmutableList<GameSfx> WORKER_SOUNDS = ImmutableList
			.of(LITTLE_WORKER,//
					LITTLE_WORKER2,//
					LITTLE_WORKER3,//
					LITTLE_WORKER4,//
					LITTLE_WORKER5,//
					LITTLE_WORKER6);
	private static final Randoms randoms = Randoms.with(new Random());

	public final Clip clip;

	private GameSfx(String path) {
		clip = BoneWarsGame.getSoundBoard().getClip(PREFIX + path);
		this.clip.setVolume(1.1f);
	}

	public void play() {
		AudioSystem.instance().play(this);
	}

	public static GameSfx randomWorkerSound() {
		return WORKER_SOUNDS.get(randoms.getInt(WORKER_SOUNDS.size()));
	}
}
