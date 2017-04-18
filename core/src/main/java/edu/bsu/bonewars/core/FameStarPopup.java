package edu.bsu.bonewars.core;

import static playn.core.PlayN.graphics;
import playn.core.GroupLayer;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.Layer;
import tripleplay.anim.Animator;
import tripleplay.ui.Label;
import tripleplay.ui.Root;
import tripleplay.ui.Style;
import tripleplay.ui.layout.AxisLayout;
import edu.bsu.bonewars.core.model.Fame;
import edu.bsu.bonewars.core.model.Player;
import edu.bsu.bonewars.core.view.BoneWarsStylesheetFactory;
import edu.bsu.bonewars.core.view.GameFont;
import edu.bsu.bonewars.core.view.GameImage;
import edu.bsu.bonewars.core.view.Palette;

public final class FameStarPopup {

	private enum Type {
		GAIN {
			@Override
			public String modifier() {
				return "+";
			}
		},
		LOSS {
			@Override
			public String modifier() {
				return "-";
			}
		};
		public abstract String modifier();
	};

	public static FameStarPopup createGainStar(Player p, Fame fame) {
		return new FameStarPopup(p, fame, Type.GAIN);
	}

	public static FameStarPopup createLossStar(Player p, Fame fame) {
		return new FameStarPopup(p, fame, Type.LOSS);
	}

	private GroupLayer.Clipped layer;
	private ImageLayer starLayer;
	private GroupLayer.Clipped textLayer;
	private final AnimationDelegate animationDelegate;

	private FameStarPopup(Player p, Fame fame, Type type) {
		if (type == Type.GAIN) {
			animationDelegate = new GainAnimation();
		} else {
			animationDelegate = new LossAnimation();
		}

		Image image = animationDelegate.image();
		starLayer = graphics().createImageLayer(image);

		layer = graphics().createGroupLayer(image.width(), image.height());
		layer.add(starLayer);
		layer.setOrigin(layer.width() / 2, layer.height() / 2);
		layer.setScale(2f);

		textLayer = graphics().createGroupLayer(image.width(), image.height());
		layer.add(textLayer);

		Root root = PlayingScreen.screen().iface.createRoot(
				AxisLayout.horizontal(), BoneWarsStylesheetFactory.sheet(),
				textLayer)//
				.setSize(layer.width(), layer.height());
		root.add(new Label(type.modifier() + fame.toInt())//
				.addStyles(Style.FONT.is(GameFont.REGULAR.atSize(16f)), //
						Style.TEXT_EFFECT.pixelOutline,//
						Style.HIGHLIGHT.is(Palette.FAME_YELLOW),//
						Style.COLOR.is(colorOf(p))));
	}

	private int colorOf(Player p) {
		return p.isMarsh() ? Palette.MARSH_BROWN : Palette.COPE_BLUE;
	}

	public Layer.HasSize layer() {
		return layer;
	}

	public void animate(Animator anim) {
		animationDelegate.animate(anim);
	}

	private interface AnimationDelegate {
		void animate(Animator anim);

		Image image();
	}

	private abstract class AbstractAnimationDelegate implements
			AnimationDelegate {
		void animateStarLayerAlpha(Animator anim, float delay) {
			anim.tweenAlpha(starLayer)//
					.from(0)//
					.to(1)//
					.in(250)//
					.easeIn()//
					.then()//
					.delay(delay)//
					.then()//
					.tweenAlpha(starLayer)//
					.to(0)//
					.in(1000f)//
					.easeOut()//
					.then()//
					.action(new Runnable() {
						@Override
						public void run() {
							layer.destroy();
						}
					});
		}

		void animateTextLayer(Animator anim, float delay) {
			anim.delay(250f)//
					.then()//
					.tweenAlpha(textLayer)//
					.from(0)//
					.to(1)//
					.in(250f)//
					.easeIn()//
					.then()//
					.delay(delay)//
					.then()//
					.tweenAlpha(textLayer)//
					.to(0)//
					.in(500f)//
					.easeOut();
		}
	}

	private final class GainAnimation extends AbstractAnimationDelegate {

		@Override
		public Image image() {
			return GameImage.FAME_STAR.image;
		}

		public void animate(Animator anim) {
			final float startx = layer.tx();
			final float starty = layer.ty();

			starLayer.setAlpha(0);
			textLayer.setAlpha(0);
			animateStarLayerAlpha(anim, 1500f);

			anim.tweenTranslation(layer)//
					.to(startx, starty - starLayer.height())//
					.in(1000f)//
					.easeIn()//
					.then()//
					.tweenTranslation(layer)//
					.to(startx, starty - starLayer.height() * 1.5f)//
					.in(1000f)//
					.easeOut();

			animateTextLayer(anim, 1500f);
		}
	}

	private final class LossAnimation extends AbstractAnimationDelegate {

		@Override
		public Image image() {
			return GameImage.BLACK_STAR.image;
		}

		@Override
		public void animate(Animator anim) {
			final float startx = layer.tx();
			final float starty = layer.ty();

			starLayer.setAlpha(0);
			textLayer.setAlpha(0);

			animateStarLayerAlpha(anim, 750f);
			animateTextLayer(anim, 750f);
			anim.tweenTranslation(layer)//
					.to(startx, starty + starLayer.height() / 2)//
					.in(1500f)//
					.easeOut();
		}

	}
}
