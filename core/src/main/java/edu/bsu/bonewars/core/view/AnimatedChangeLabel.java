package edu.bsu.bonewars.core.view;

import playn.core.Color;
import pythagoras.f.IDimension;
import react.ValueView;
import react.ValueView.Listener;
import tripleplay.anim.AnimGroup;
import tripleplay.anim.Animation;
import tripleplay.anim.Animator;
import tripleplay.ui.Label;
import tripleplay.ui.Style;
import edu.bsu.bonewars.core.PlayingScreen;

public abstract class AnimatedChangeLabel extends Label {

	private final Animation.Value fromBlackToColor;
	private final Animation.Value fromColorToBlack;

	public AnimatedChangeLabel(IDimension size, ValueView<Integer> value,
			int color) {
		fromBlackToColor = new ColorAnimation(color, 0);
		fromColorToBlack = new ColorAnimation(color, 255);

		setSize(size.width(), size.height());
		layer.setOrigin(size.width() / 2, size.height() / 2);
		updateText(value.get().intValue());
		value.connect(new Listener<Integer>() {
			@Override
			public void onChange(final Integer newValue, final Integer oldValue) {
				Animation.Value animatedValue = new Animation.Value() {
					@Override
					public void set(float value) {
						updateText((int) value);
					}

					@Override
					public float initial() {
						return oldValue.intValue();
					}
				};
				final float zoomDuration = 200f;
				Animator anim = PlayingScreen.screen().anim;
				AnimGroup zoomInAndTurnToColor = new AnimGroup();
				zoomInAndTurnToColor.tween(fromBlackToColor)//
						.from(0f)//
						.to(1f)//
						.in(zoomDuration)//
						.easeIn();
				zoomInAndTurnToColor.tweenScale(layer)//
						.to(1.1f)//
						.in(zoomDuration)//
						.easeIn();

				AnimGroup zoomOutAndTurnBlack = new AnimGroup();
				zoomOutAndTurnBlack.tween(fromColorToBlack)//
						.from(1f)//
						.to(0f)//
						.in(zoomDuration)//
						.easeOut();
				zoomOutAndTurnBlack.tweenScale(layer)//
						.to(1.0f)//
						.in(zoomDuration)//
						.easeOut();

				anim.add(zoomInAndTurnToColor.toAnim())//
						.then()//
						.tween(animatedValue)//
						.to(newValue.intValue())//
						.in(500f)//
						.then()//
						.delay(200f)//
						.then()//
						.add(zoomOutAndTurnBlack.toAnim());
			};
		});
	}

	protected void updateText(int i) {
		text.update("" + i);
	}

	private final class ColorAnimation implements Animation.Value {

		private final int initial;
		private final int color;

		private ColorAnimation(int color, int initial) {
			this.color = color;
			this.initial = initial;
		}

		@Override
		public float initial() {
			return initial;
		}

		@Override
		public void set(float value) {
			int newColor = Color.rgb((int) (Color.red(color) * value),
					(int) (Color.green(color) * value),
					(int) (Color.blue(color) * value));

			addStyles(Style.COLOR.is(newColor));
		}

	}

}
