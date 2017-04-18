package edu.bsu.bonewars.core.view;

import static playn.core.PlayN.graphics;
import playn.core.Font;
import playn.core.GroupLayer;
import playn.core.Layer;
import pythagoras.f.Point;
import pythagoras.i.Dimension;
import pythagoras.i.IDimension;
import react.Slot;
import react.UnitSlot;
import react.ValueView.Listener;
import tripleplay.anim.Animation;
import tripleplay.ui.Background;
import tripleplay.ui.Button;
import tripleplay.ui.Constraints;
import tripleplay.ui.Element;
import tripleplay.ui.Group;
import tripleplay.ui.Icons;
import tripleplay.ui.Label;
import tripleplay.ui.Root;
import tripleplay.ui.Style;
import tripleplay.ui.layout.AxisLayout;
import edu.bsu.bonewars.core.PlayingScreen;
import edu.bsu.bonewars.core.model.FossilHunterAuction;
import edu.bsu.bonewars.core.model.Funds;
import edu.bsu.bonewars.core.model.Game;
import edu.bsu.bonewars.core.model.Player;

public class FossilHunterDialog extends AbstractDialog {

	private static final IDimension SIZE = new Dimension(440, 330);
	private static final Font FONT = GameFont.REGULAR.atSize(22);
	private static final float COUNTDOWN = 3f;

	public static FossilHunterDialog create() {
		return new FossilHunterDialog();
	}

	private final GroupLayer.Clipped layer;
	public final FossilHunterAuction auction = FossilHunterAuction.create();
	private GroupLayer fossilLayer;
	private Button doneButton;

	private FossilHunterDialog() {
		this.layer = graphics().createGroupLayer(SIZE.width(), SIZE.height());

		AudioSystem.instance().play(GameMusic.FOSSIL_HUNTER_THEME);

		Root root = PlayingScreen.screen().iface
				.createRoot(AxisLayout.vertical(),
						BoneWarsStylesheetFactory.sheet(), layer)//
				.setSize(SIZE.width(), SIZE.height())
				.addStyles(
						Style.BACKGROUND.is(Background
								.image(GameImage.DIALOG_BG.image)));

		initDoneButton();
		root.add(new Label("A fossil hunter is selling this fossil:")//
				.addStyles(Style.FONT.is(FONT),//
						Style.TEXT_WRAP.on), //
				makeFossilImageGroup(), //
				new Label("Who will buy it, and for how much?")//
						.addStyles(Style.FONT.is(FONT)), //
				createBidArea(),//
				doneButton);
	}

	private Element<?> createBidArea() {
		return new Group(AxisLayout.horizontal())//
				.add(new BidButton(Game.currentGame().marsh),//
						createCenterWidget(), //
						new BidButton(Game.currentGame().cope));
	}

	private Element<?> createCenterWidget() {
		final Label label = new Label("No bids yet...")//
				.addStyles(Style.FONT.is(FONT),//
						Style.HALIGN.center,//
						Style.TEXT_WRAP.on)//
				.setConstraint(Constraints.fixedWidth(SIZE.width() / 3));
		auction.onBid(new UnitSlot() {
			@Override
			public void onEmit() {
				label.text.update(auction.highBidder.get().name()
						+ " will\npay $" + auction.highBid.get().asInt() + "!");
			}
		});
		return label;
	}

	private void initDoneButton() {
		doneButton = new Button("").addStyles(Style.FONT.is(FONT))
				.setConstraint(Constraints.fixedWidth(SIZE.width() / 5))
				.setEnabled(false);
		doneButton.onClick(new Slot<Button>() {
			@Override
			public void onEmit(Button event) {
				FossilHunterDialog.this.onClose.emit();
				auction.complete();
			}
		});
		auction.highBid.connect(new Listener<Funds>() {
			@Override
			public void onChange(Funds value, Funds oldValue) {
				runCountdown();
			}
		});
		runCountdown();
	}

	private final Animation.Value countdown = new Animation.Value() {
		@Override
		public float initial() {
			return COUNTDOWN;
		}

		@Override
		public void set(float value) {
			doneButton.text.update("" + (int) (value + 1));
		}
	};

	private Animation.Handle countdownAnimationHandle;

	private void runCountdown() {
		doneButton.setEnabled(false);
		if (countdownAnimationHandle != null) {
			countdownAnimationHandle.cancel();
		}
		Animation animation = PlayingScreen.screen().anim//
				.tween(countdown)//
				.to(0)//
				.in(COUNTDOWN * 1000f);
		countdownAnimationHandle = animation.handle();
		animation.then()//
				.action(new Runnable() {
					@Override
					public void run() {
						countdownAnimationHandle = null;
						doneButton.text.update("Done!");
						doneButton.setEnabled(true);
					}
				});
	}

	private Group makeFossilImageGroup() {
		Group group = new Group(AxisLayout.horizontal());
		group.add(
				new Label(Icons.image(GameImage
						.getGameImageForFossilType(auction.fossil.type()))), //
				new Label(Icons.image(GameImage.completenessFor(auction.fossil
						.quality()))));
		fossilLayer = group.layer;
		return group;
	}

	@Override
	public Layer.HasSize layer() {
		return layer;
	}

	public Point fossilLayerLocation() {
		return new Point(fossilLayer.tx(), fossilLayer.ty());
	}

	private final class BidButton extends Button {
		private BidButton(final Player player) {
			updateText();
			icon.update(Icons.image(player.isMarsh() ? GameImage.FOSSIL_HUNTER_MARSH.image
					: GameImage.FOSSIL_HUNTER_COPE.image));
			addStyles(Style.ICON_POS.above,//
					Style.FONT.is(FONT));
			onClick(new Slot<Button>() {
				@Override
				public void onEmit(Button event) {
					auction.bid(player);
				}
			});
			auction.nextBidAmount.connect(new Listener<Funds>() {
				@Override
				public void onChange(Funds value, Funds oldValue) {
					setEnabled(auction.canBid(player));
					updateText();
				}
			});
		}

		private void updateText() {
			String mesg;
			if (isEnabled()) {
				mesg = "Bid $" + auction.nextBidAmount.get().asInt();
			} else {
				mesg = "Cannot bid!";
			}
			text.update(mesg);
		}
	}

}
