package edu.bsu.bonewars.core.view;

import static com.google.common.base.Preconditions.checkNotNull;
import static playn.core.PlayN.graphics;
import playn.core.GroupLayer;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.Layer;
import playn.core.Pointer;
import playn.core.Pointer.Event;
import react.ValueView.Listener;
import tripleplay.ui.Label;
import tripleplay.ui.Root;
import tripleplay.ui.layout.AxisLayout;
import tripleplay.util.Input.Registration;
import edu.bsu.bonewars.core.PlayingScreen;
import edu.bsu.bonewars.core.actions.RaiseFundsAction;

public final class RaiseFundsFlag {

	public static RaiseFundsFlag create(RaiseFundsAction action) {
		return new RaiseFundsFlag(action);
	}

	private GroupLayer layer;
	private final RaiseFundsAction action;

	private final Pointer.Listener clickListener = new Pointer.Adapter() {
		@Override
		public void onPointerStart(Event event) {
			GameSfx.RAISE_FUNDS.play();
			action.doAction();
		}
	};

	private RaiseFundsFlag(final RaiseFundsAction action) {
		this.action = checkNotNull(action);
		layer = graphics().createGroupLayer();
		layer.setDepth(UiDepth.POPUP.value);
		Image raiseFundsImage = GameImage.RAISE_fUNDS.image;
		final ImageLayer imageLayer = graphics().createImageLayer(
				raiseFundsImage);
		layer.add(imageLayer);
		imageLayer.setInteractive(false);

		Root root = PlayingScreen.screen().iface.createRoot(
				AxisLayout.horizontal(), BoneWarsStylesheetFactory.sheet(),
				layer)//
				.setSize(imageLayer.width(), imageLayer.height());
		root.add(new Label("+$"
				+ RaiseFundsAction.AMOUNT_GAINED_THROUGH_ACTION.asInt()));

		action.available().connect(new Listener<Boolean>() {
			@Override
			public void onChange(Boolean available, Boolean oldValue) {
				if (available) {
					attachClickListener();
					PlayingScreen.screen().anim.tweenY(layer)//
							.to(layer.ty() + imageLayer.height())//
							.in(AnimationConstants.FLAG_POPOUT_TIME)//
							.easeIn();
				} else {
					detachClickListener();
					PlayingScreen.screen().anim.tweenY(layer)//
							.to(layer.ty() - imageLayer.height())//
							.in(AnimationConstants.FLAG_POPOUT_TIME)//
							.easeIn();
				}
			}

			private Registration registration;

			private void attachClickListener() {
				this.registration = PlayingScreen.screen().pInput.register(
						imageLayer, clickListener);
			}

			private void detachClickListener() {
				registration.cancel();
			}
		});

	}

	public Layer layer() {
		return layer;
	}

}
