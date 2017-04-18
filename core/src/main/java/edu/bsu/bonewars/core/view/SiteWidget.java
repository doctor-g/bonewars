package edu.bsu.bonewars.core.view;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static edu.bsu.bonewars.core.PlayingScreen.screen;
import static playn.core.PlayN.graphics;
import playn.core.GroupLayer;
import playn.core.ImageLayer;
import playn.core.Layer;
import playn.core.Pointer;
import playn.core.Pointer.Event;
import pythagoras.i.Dimension;
import pythagoras.i.IDimension;
import pythagoras.i.IPoint;
import pythagoras.i.Point;
import react.Slot;
import react.ValueView.Listener;
import tripleplay.ui.Label;
import tripleplay.ui.Root;
import tripleplay.ui.Style;
import tripleplay.ui.layout.AbsoluteLayout;
import tripleplay.util.Colors;
import tripleplay.util.Input.Registration;
import edu.bsu.bonewars.core.PlayingScreen;
import edu.bsu.bonewars.core.actions.AcquireSiteAction;
import edu.bsu.bonewars.core.actions.ExcavateAction;
import edu.bsu.bonewars.core.model.Game;
import edu.bsu.bonewars.core.model.Site;

public final class SiteWidget {

	public static final IDimension SIZE = new Dimension(
			(int) (GameImage.SITE_UNCLAIMED.image.width() + GameImage.ACQUIRE_FLAG.image
					.width()), //
			(int) GameImage.SITE_UNCLAIMED.image.height());
	public static final IPoint FOSSIL_COUNT_LOCATION = new Point(30, 16);

	public static SiteWidget createFor(Site site) {
		return new SiteWidget(site);
	}

	private final GroupLayer.Clipped layer;
	private final AcquireSiteActionFlag acquireActionFlag;
	private final Layer.HasSize excavateActionFlag;
	private final AcquireSiteAction acquireAction;
	private final ExcavateAction excavationAction;
	private Registration inputRegistration;
	private final Root root;
	private final Site site;
	private final ImageLayer imageLayer;

	private SiteWidget(final Site site) {
		this.site = checkNotNull(site);
		this.layer = graphics().createGroupLayer(SIZE.width(), SIZE.height());
		imageLayer = graphics()
				.createImageLayer(GameImage.SITE_UNCLAIMED.image);
		this.layer.add(imageLayer);

		this.acquireAction = Game.currentGame().actionRegistry()
				.getAcquireSiteActionForSite(site);

		acquireActionFlag = AcquireSiteActionFlag.createFor(site);

		acquireAction.available().connect(new Listener<Boolean>() {
			@Override
			public void onChange(Boolean value, Boolean oldValue) {
				if (value) {
					popupAcquireActionFlag();
				} else {
					removeAcquirePopup();
				}
			}
		});

		this.excavateActionFlag = graphics().createImageLayer(
				GameImage.EXCAVATION_ICON.image);
		this.excavationAction = Game.currentGame().actionRegistry()
				.getExcavateActionForSite(site);
		excavationAction.available().connect(new Listener<Boolean>() {
			@Override
			public void onChange(Boolean value, Boolean oldValue) {
				if (value) {
					popupExcavationFlag();
				} else {
					removeExcavatePopup();
				}
			}
		});

		setImageBasedOnOwner();
		site.hasOwner().connect(new Slot<Boolean>() {
			@Override
			public void onEmit(Boolean player) {
				setImageBasedOnOwner();
			}
		});

		this.root = screen().iface.createRoot(new AbsoluteLayout(),
				BoneWarsStylesheetFactory.sheet(), layer)//
				.setSize(layer.width(), layer.height());
		initNumberOfFossilsLabel();
	}

	private void setImageBasedOnOwner() {
		if (site.hasOwner().get()) {
			if (site.owner().isCope()) {
				imageLayer.setImage(GameImage.SITE_OWNED_BY_COPE.image);
			} else if (site.owner().isMarsh()) {
				imageLayer.setImage(GameImage.SITE_OWNED_BY_MARSH.image);
			}
		}
	}

	private void initNumberOfFossilsLabel() {
		Label siteNumberOfFossilsLabel = NumberOfFossilsLabel.createFor(site)//
				.addStyles(Style.COLOR.is(Colors.WHITE));
		root.add(AbsoluteLayout.at(siteNumberOfFossilsLabel,
				FOSSIL_COUNT_LOCATION.x(), FOSSIL_COUNT_LOCATION.y()));
	}

	public Layer.HasSize layer() {
		return layer;
	}

	private void popupAcquireActionFlag() {
		layer.parent().add(acquireActionFlag.layer());
		checkState(layer.depth() == UiDepth.SITE.value, //
				"Layer depth is " + layer.depth());
		acquireActionFlag.layer().setDepth(UiDepth.SITE_POPUNDER.value);

		float rightSideOfSiteWidget = rightSide();
		acquireActionFlag.layer().setTranslation(
				rightSideOfSiteWidget - acquireActionFlag.layer().width(),
				layer.ty());
		PlayingScreen.screen().anim.tweenX(acquireActionFlag.layer())//
				.to(rightSideOfSiteWidget)//
				.in(AnimationConstants.FLAG_POPOUT_TIME)//
				.easeIn()//
				.then()//
				.action(new Runnable() {
					@Override
					public void run() {
						inputRegistration = screen().pInput.register(
								acquireActionFlag.layer(),
								new Pointer.Adapter() {
									@Override
									public void onPointerStart(Event event) {
										GameSfx.ACQUIRE.play();
										acquireAction.doAction();
									}
								});
					}
				});
	}

	private float rightSide() {
		return layer.tx() + GameImage.SITE_UNCLAIMED.image.width();
	}

	private void removeAcquirePopup() {
		inputRegistration.cancel();
		PlayingScreen.screen().anim.tweenX(acquireActionFlag.layer())//
				.to(rightSide() - acquireActionFlag.layer().width())//
				.in(AnimationConstants.FLAG_POPOUT_TIME)//
				.easeIn()//
				.then()//
				.action(new Runnable() {
					@Override
					public void run() {
						acquireActionFlag.layer().parent()
								.remove(acquireActionFlag.layer());
					}
				});
	}

	private void popupExcavationFlag() {
		layer.parent().add(excavateActionFlag);
		excavateActionFlag.setTy(layer.ty());
		PlayingScreen.screen().anim.tweenX(excavateActionFlag)//
				.from(rightSide() - excavateActionFlag.width())//
				.to(rightSide())//
				.in(AnimationConstants.FLAG_POPOUT_TIME)//
				.easeIn()//
				.then()//
				.action(new Runnable() {
					@Override
					public void run() {
						inputRegistration = screen().pInput.register(
								excavateActionFlag, new Pointer.Adapter() {
									@Override
									public void onPointerStart(Event event) {
										GameSfx.EXCAVATE.play();
										excavationAction.doAction();
									}
								});
					}
				});

	}

	private void removeExcavatePopup() {
		inputRegistration.cancel();
		PlayingScreen.screen().anim.tweenX(excavateActionFlag)//
				.to(rightSide() - acquireActionFlag.layer().width())//
				.in(AnimationConstants.FLAG_POPOUT_TIME)//
				.easeIn()//
				.then()//
				.action(new Runnable() {
					@Override
					public void run() {
						excavateActionFlag.parent().remove(excavateActionFlag);
					}
				});
	}

}
