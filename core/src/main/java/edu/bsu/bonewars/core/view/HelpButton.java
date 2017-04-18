package edu.bsu.bonewars.core.view;

import pythagoras.i.Dimension;
import react.Connection;
import react.Slot;
import tripleplay.anim.AnimGroup;
import tripleplay.anim.Animation;
import tripleplay.ui.Background;
import tripleplay.ui.Button;
import tripleplay.ui.Style;
import edu.bsu.bonewars.core.PlayingScreen;
import edu.bsu.bonewars.core.model.Game;

public class HelpButton extends Button {

	public static final Dimension SIZE = new Dimension(30, 30);

	private boolean hasBeenClicked = false;

	public HelpButton() {
		super("?");
		addStyles(Style.BACKGROUND.is(Background.roundRect(0x000000, 5f,
				Palette.WOOD_YELLOW, 2f)));
		if (isThisTheFirstGame()) {
			configurePulseAnimation();
		}
		onClick(new Slot<Button>() {
			@Override
			public void onEmit(Button event) {
				hasBeenClicked = true;
			}
		});
	}

	private boolean isThisTheFirstGame() {
		return Game.playedGames().get() == 1;
	}

	private Connection connection;
	private Animation animation;

	private void configurePulseAnimation() {
		connection = hierarchyChanged().connect(new Slot<Boolean>() {
			@Override
			public void onEmit(Boolean event) {
				final AnimGroup animGroup = new AnimGroup();
				animGroup.delay(2000f)//
						.then()//
						.tweenAlpha(layer)//
						.to(0f)//
						.in(500f)//
						.bounceOut()//
						.then()//
						.tweenAlpha(layer)//
						.to(1f)//
						.in(500f)//
						.easeOut()//
						.then()//
						.action(new Runnable() {
							@Override
							public void run() {
								if (shouldAnimationContinue()) {
									PlayingScreen.screen().anim.add(animation);
								} else {
									connection.disconnect();
								}
							}

							private boolean shouldAnimationContinue() {
								return !hasBeenClicked //
										&& isThisTheFirstGame() //
										&& isThisTheFirstRound();
							}

							private boolean isThisTheFirstRound() {
								return Game.currentGame().round().get() == 1;
							}
						});
				animation = animGroup.toAnim();
				PlayingScreen.screen().anim.add(animation);
			}
		});
	}
}
