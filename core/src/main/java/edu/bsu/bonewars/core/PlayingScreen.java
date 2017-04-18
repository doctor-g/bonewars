package edu.bsu.bonewars.core;

import static com.google.common.base.Preconditions.checkState;
import static playn.core.PlayN.graphics;
import static playn.core.PlayN.keyboard;
import static playn.core.PlayN.pointer;
import playn.core.CanvasImage;
import playn.core.GroupLayer;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.Layer;
import playn.core.util.Callback;
import pythagoras.f.Dimension;
import pythagoras.f.Point;
import react.Slot;
import react.UnitSlot;
import react.ValueView.Listener;
import tripleplay.game.ScreenStack;
import tripleplay.game.UIAnimScreen;
import tripleplay.game.trans.FadeTransition;
import tripleplay.ui.Background;
import tripleplay.ui.Button;
import tripleplay.ui.Label;
import tripleplay.ui.Root;
import tripleplay.ui.Style;
import tripleplay.ui.Styles;
import tripleplay.ui.layout.AbsoluteLayout;
import tripleplay.util.Colors;
import tripleplay.util.Layers;
import tripleplay.util.PointerInput;
import edu.bsu.bonewars.core.actions.RaiseFundsAction;
import edu.bsu.bonewars.core.event.BouncedPublishAttempt;
import edu.bsu.bonewars.core.event.ExcavationEvent;
import edu.bsu.bonewars.core.event.GameEvent;
import edu.bsu.bonewars.core.event.GameEventHandler;
import edu.bsu.bonewars.core.event.PublicationEvent;
import edu.bsu.bonewars.core.model.Fossil;
import edu.bsu.bonewars.core.model.Game;
import edu.bsu.bonewars.core.model.Game.StoryEventHandler;
import edu.bsu.bonewars.core.model.Player;
import edu.bsu.bonewars.core.model.StoryEvent;
import edu.bsu.bonewars.core.view.AnimationConstants;
import edu.bsu.bonewars.core.view.AudioSystem;
import edu.bsu.bonewars.core.view.BoneWarsStylesheetFactory;
import edu.bsu.bonewars.core.view.CheatCodeListener;
import edu.bsu.bonewars.core.view.CopeWorkerPanel;
import edu.bsu.bonewars.core.view.FameLabel;
import edu.bsu.bonewars.core.view.FossilCollectionWidget;
import edu.bsu.bonewars.core.view.FossilHunterDialog;
import edu.bsu.bonewars.core.view.FossilStackWidget;
import edu.bsu.bonewars.core.view.FundsLabel;
import edu.bsu.bonewars.core.view.GameFont;
import edu.bsu.bonewars.core.view.GameImage;
import edu.bsu.bonewars.core.view.GameOverWidget;
import edu.bsu.bonewars.core.view.GameSfx;
import edu.bsu.bonewars.core.view.HelpButton;
import edu.bsu.bonewars.core.view.HireWorkerWidget;
import edu.bsu.bonewars.core.view.MapPanel;
import edu.bsu.bonewars.core.view.MarshWorkerPanel;
import edu.bsu.bonewars.core.view.Palette;
import edu.bsu.bonewars.core.view.PublishedFossilWidget;
import edu.bsu.bonewars.core.view.PublishedFossilsAreaWidget;
import edu.bsu.bonewars.core.view.RaiseFundsFlag;
import edu.bsu.bonewars.core.view.RoundCounterWidget;
import edu.bsu.bonewars.core.view.SiteWidget;
import edu.bsu.bonewars.core.view.StoryCard;
import edu.bsu.bonewars.core.view.UiDepth;
import edu.bsu.bonewars.core.view.UpkeepDialog;

public class PlayingScreen extends UIAnimScreen {

	private static final boolean CHEATING_ENABLED = true;
	private static final int FOSSIL_COLLECTION_Y = 88;

	public static PlayingScreen screen() {
		return mostRecentInstance;
	}

	private static PlayingScreen mostRecentInstance;

	public final PointerInput pInput = new PointerInput();
	private final ScreenStack screenStack;
	private RoundCounterWidget roundLabel;
	private MapPanel mapPanel;
	private FossilCollectionWidget marshFossilCollection;
	private FossilCollectionWidget copeFossilCollection;
	private PublishedFossilsAreaWidget publishingArea;
	private Root root;
	private final Game game;

	public static PlayingScreen create(ScreenStack screenStack) {
		return new PlayingScreen(screenStack);
	}

	private PlayingScreen(ScreenStack screenStack) {
		this.screenStack = screenStack;
		mostRecentInstance = this;
		this.game = Game.create();
		init();
		if (CHEATING_ENABLED) {
			keyboard().setListener(new CheatCodeListener());
		}
	}

	private void init() {
		AudioSystem.instance().playTheme();

		layer.setDepth(UiDepth.BOTTOM.value);
		initBackground();
		initTPUI();

		MarshWorkerPanel marshWorkerPanel = new MarshWorkerPanel();
		layer.addAt(marshWorkerPanel.layer(), 182, 4);

		marshFossilCollection = FossilCollectionWidget.forPlayer(game.marsh);
		layer.addAt(marshFossilCollection.layer(), 15, FOSSIL_COLLECTION_Y);

		CopeWorkerPanel copeWorkerPanel = new CopeWorkerPanel();
		layer.addAt(copeWorkerPanel.layer(), 424, 4);

		copeFossilCollection = FossilCollectionWidget.forPlayer(game.cope);
		layer.addAt(copeFossilCollection.layer(), 618, FOSSIL_COLLECTION_Y);

		RaiseFundsAction raiseFundsAction = RaiseFundsAction.create();
		RaiseFundsFlag raiseFundsFlag = RaiseFundsFlag.create(raiseFundsAction);
		layer.addAt(raiseFundsFlag.layer(), 350, 78);
		raiseFundsFlag.layer().setDepth(UiDepth.HUD_POPUNDER.value);

		mapPanel = MapPanel.create().enableAnimation();
		layer.addAt(mapPanel.layer(), 190, 80);

		publishingArea = PublishedFossilsAreaWidget.create(Game.currentGame()
				.publishedFossilsArea());
		layer.addAt(publishingArea.layer(), 4, 522);

		HireWorkerWidget hireWorkerWidget = new HireWorkerWidget();
		layer.addAt(hireWorkerWidget.layer(), 384, 5);

		roundLabel = RoundCounterWidget.create();
		layer.addAt(roundLabel.layer(), 341, 80);

		connectForGameOver();
		setToHudLayer(marshFossilCollection.layer(), //
				copeFossilCollection.layer(), //
				marshWorkerPanel.layer(),//
				copeWorkerPanel.layer(),//
				publishingArea.layer(),//
				roundLabel.layer(),//
				mapPanel.layer());

		game.setStoryEventHandler(new StoryEventHandler() {
			@Override
			public void handleStoryEvent(final StoryEvent storyEvent,
					final Callback<Void> onComplete) {
				StoryCard card = StoryCard.createFor(storyEvent);
				card.onClose(new UnitSlot() {
					@Override
					public void onEmit() {
						AudioSystem.instance().playTheme();
						onComplete.onSuccess(null);
					}
				});
				card.show();
			}
		});

		game.setUpkeepHandler(new Game.UpkeepHandler() {
			@Override
			public void timeForUpkeep(final Callback<Void> callback) {
				UpkeepDialog upkeepDialog = showUpkeepDialog();
				upkeepDialog.onClose(new UnitSlot() {
					@Override
					public void onEmit() {
						callback.onSuccess(null);
					}
				});
			}
		});

		game.workerSelectionModel().onChange().connect(new UnitSlot() {
			@Override
			public void onEmit() {
				game.workerSelectionModel().disableSelectionChange();
				float animationSmudgeValue = 50.0f;
				anim.delay(
						AnimationConstants.FLAG_POPOUT_TIME
								+ animationSmudgeValue).then()
						.action(new Runnable() {
							@Override
							public void run() {
								game.workerSelectionModel()
										.enableSelectionChange();
							}
						});

			}
		});

		game.setAuctionHandler(new Game.AuctionHandler() {
			@Override
			public void timeForAuction(final Callback<Void> callback) {
				final FossilHunterDialog dialog = FossilHunterDialog.create();
				dialog.show();
				dialog.onClose(new UnitSlot() {
					@Override
					public void onEmit() {
						AudioSystem.instance().playTheme();
						if (dialog.auction.hasBids()) {
							animateAcquiredFossilToOwnerCollection();
						} else {
							callback.onSuccess(null);
						}
					}

					private void animateAcquiredFossilToOwnerCollection() {
						Point p = dialog.fossilLayerLocation();
						p = Layers.transform(p, dialog.layer(), layer);

						Layer animatedLayer = makeAnimatableLayerFor(dialog.auction.fossil);

						animatedLayer.setTranslation(p.x(), p.y())//
								.setDepth(UiDepth.POPUP.value);
						layer.add(animatedLayer);

						FossilCollectionWidget collectionWidget = dialog.auction.highBidder
								.get().isCope() ? copeFossilCollection
								: marshFossilCollection;
						Point to = collectionWidget
								.pointFor(dialog.auction.fossil.type());
						to.addLocal(
								FossilStackWidget.FOSSIL_ICON_MARGIN.width(),
								FossilStackWidget.FOSSIL_ICON_MARGIN.height());

						anim.tweenTranslation(animatedLayer).to(to).easeInOut()
								.in(AnimationConstants.FOSSIL_MIGRATION_TIME)//
								.then()//
								.action(new Runnable() {
									@Override
									public void run() {
										callback.onSuccess(null);
									}
								});
						animateScaleForFossilMigration(animatedLayer);
					}
				});
			}
		});

		game.round().connect(new Listener<Integer>() {
			@Override
			public void onChange(Integer value, Integer oldValue) {
				GameSfx.ROUND_CHANGE.play();
			}
		});

		initNamePlacards();
		configureExcavationAnimation();
		configurePublicationAnimation();
		configureBouncedPublishAttemptAnimation();
		initHelpButton();
	}

	private void initNamePlacards() {
		final Dimension placardSize = new Dimension(76, 16);
		final float y = 64;
		final Styles styles = Styles.make(Style.FONT.is(GameFont.REGULAR
				.atSize(16f)),//
				Style.BACKGROUND.is(Background.bordered(Palette.WOOD_YELLOW,
						Palette.EARTHY_BROWN, 2)),//
				Style.COLOR.is(Palette.EARTHY_BROWN));
		Label marsh = new Label("O.C. Marsh")//
				.addStyles(styles);
		root.add(AbsoluteLayout.at(marsh, new Point(10, y), placardSize));
		Label cope = new Label("E.D. Cope")//
				.addStyles(styles);
		root.add(AbsoluteLayout.at(cope, new Point(714, y), placardSize));
	}

	private void configureExcavationAnimation() {
		game.eventBus.register(new GameEventHandler() {
			private final GameEvent.Visitor<Void> excavationAnimator = new GameEvent.Visitor.Adapter<Void>() {
				@Override
				public Void visit(ExcavationEvent event, Object... args) {
					animateExcavation(event);
					return null;
				}
			};

			@Override
			public void handle(GameEvent event) {
				event.accept(excavationAnimator);
			}

			private void animateExcavation(ExcavationEvent event) {
				final float duration = AnimationConstants.FOSSIL_MIGRATION_TIME;
				final Layer animatedLayer = makeAnimatableLayerFor(event.fossil);

				SiteWidget sourceSiteWidget = mapPanel.widgetFor(event.site);
				checkState(sourceSiteWidget != null);

				Point from = Layers.transform(new Point(sourceSiteWidget
						.layer().tx(), sourceSiteWidget.layer().ty()),
						sourceSiteWidget.layer().parent(), layer);

				FossilCollectionWidget destinationWidget = event.player
						.isMarsh() ? marshFossilCollection
						: copeFossilCollection;
				Point to = destinationWidget.pointFor(event.fossil.type())//
						.add(FossilStackWidget.FOSSIL_ICON_MARGIN.width(),
								FossilStackWidget.FOSSIL_ICON_MARGIN.height());

				layer.add(animatedLayer);
				animatedLayer.setDepth(UiDepth.POPUP.value)//
						.setScale(0.25f);
				anim.tweenTranslation(animatedLayer)//
						.from(from)//
						.to(to)//
						.in(duration)//
						.easeInOut()//
						.then()//
						.delay(AnimationConstants.FOSSIL_WIDGET_FADEIN_TIME)//
						.then()//
						.action(new Runnable() {
							@Override
							public void run() {
								animatedLayer.destroy();
							}
						});
				animateScaleForFossilMigration(animatedLayer);
			}

		});
	}

	private Layer makeAnimatableLayerFor(Fossil fossil) {
		Image image = GameImage.getGameImageForFossilType(fossil.type());
		GroupLayer group = graphics().createGroupLayer(image.width(),
				image.height());
		CanvasImage bgImage = graphics().createImage(image.width(),
				image.height());
		bgImage.canvas().setFillColor(Colors.WHITE);
		bgImage.canvas().fillRect(0, 0, bgImage.width(), bgImage.height());
		ImageLayer bgImageLayer = graphics().createImageLayer(bgImage);
		group.add(bgImageLayer);
		group.add(graphics().createImageLayer(image));
		return group;
	}

	private void animateScaleForFossilMigration(Layer animatedLayer) {
		anim.tweenScale(animatedLayer)//
				.to(1.2f)//
				.in(AnimationConstants.FOSSIL_MIGRATION_TIME / 2f)//
				.easeIn()//
				.then()//
				.tweenScale(animatedLayer)//
				.to(1f)//
				.in(AnimationConstants.FOSSIL_MIGRATION_TIME / 2f)//
				.easeOut();
	}

	private void configurePublicationAnimation() {
		game.eventBus.register(new GameEventHandler() {

			private FameStarPopup fameStar;
			private FameStarPopup lossStar;

			private GameEvent.Visitor<Void> publicationAnimator = new GameEvent.Visitor.Adapter<Void>() {
				@Override
				public Void visit(PublicationEvent event, Object... args) {
					final PublishedFossilWidget widget = PublishedFossilWidget
							.create(event.fossil);
					Layer animatedLayer = widget.layer();
					layer.add(animatedLayer);
					animatedLayer.setDepth(UiDepth.POPUP.value);

					FossilCollectionWidget fossilCollection = event.player
							.isMarsh() ? marshFossilCollection
							: copeFossilCollection;

					Point from = fossilCollection.pointFor(event.fossil.type());

					Point to = Layers.transform(PublishedFossilsAreaWidget
							.pointFor(event.fossil.type()), //
							publishingArea.layer(),//
							layer);

					configureFameStarsFor(to, event);

					anim.tweenTranslation(animatedLayer)//
							.from(from)//
							.to(to)//
							.in(AnimationConstants.FOSSIL_MIGRATION_TIME)//
							.easeInOut()//
							.then()//
							.action(new Runnable() {
								@Override
								public void run() {
									publishingArea.add(widget);
									addAndAnimateStars();
								}

								private void addAndAnimateStars() {
									layer.add(fameStar.layer());
									fameStar.animate(anim);
									if (lossStar != null) {
										layer.add(lossStar.layer());
										lossStar.animate(anim);
									}
								}
							});
					anim.tweenScale(animatedLayer)//
							.to(1.1f)//
							.in(AnimationConstants.FOSSIL_MIGRATION_TIME / 2f)//
							.easeIn()//
							.then()//
							.tweenScale(animatedLayer)//
							.to(1f)//
							.in(AnimationConstants.FOSSIL_MIGRATION_TIME / 2f)//
							.easeOut();

					return null;
				}

				private void configureFameStarsFor(Point fossilDestination,
						PublicationEvent event) {
					if (event.isTrump) {
						float leftX = fossilDestination.x + 18;
						float rightX = leftX
								+ GameImage.FAME_STAR.image.width() + 15;

						configureGainStar(
								event, //
								event.player.isMarsh() ? leftX : rightX,
								fossilDestination.y);
						configureLossStar(
								event,//
								event.player.isMarsh() ? rightX : leftX,
								fossilDestination.y);
					} else {
						configureGainStar(event, fossilDestination.x
								+ PublishedFossilWidget.SIZE.width() / 2,
								fossilDestination.y);
					}
				}

				private void configureGainStar(PublicationEvent event, float x,
						float y) {
					fameStar = FameStarPopup.createGainStar(event.player,
							event.fame);
					fameStar.layer().setTranslation(x, y)
							.setDepth(UiDepth.POPUP.value);
					fameStar.animate(anim);
				}

				private void configureLossStar(PublicationEvent event, float x,
						float y) {
					lossStar = FameStarPopup.createLossStar(
							event.player.opponent(), event.loss);
					lossStar.layer().setTranslation(x, y)//
							.setDepth(UiDepth.POPUP.value);
				}
			};

			@Override
			public void handle(GameEvent event) {
				event.accept(publicationAnimator);
			}
		});
	}

	private void configureBouncedPublishAttemptAnimation() {
		game.eventBus.register(new GameEventHandler() {
			private GameEvent.Visitor<Void> bounceAnimator = new GameEvent.Visitor.Adapter<Void>() {
				@Override
				public Void visit(BouncedPublishAttempt event, Object... args) {
					final PublishedFossilWidget widget = PublishedFossilWidget
							.create(event.fossil);
					final Layer animatedLayer = widget.layer();
					layer.add(animatedLayer);
					animatedLayer.setDepth(UiDepth.POPUP.value);

					FossilCollectionWidget fossilCollection = event.player
							.isMarsh() ? marshFossilCollection
							: copeFossilCollection;

					Point from = fossilCollection.pointFor(event.fossil.type());

					Point to = Layers.transform(
							PublishedFossilsAreaWidget.pointFor(event.fossil
									.type()), //
							publishingArea.layer(),//
							layer)//
							.subtractLocal(0,
									PublishedFossilWidget.SIZE.height());

					final FameStarPopup fameStarPopup = FameStarPopup
							.createLossStar(event.player, event.penalty);
					fameStarPopup
							.layer()
							.setTranslation(
									to.x + PublishedFossilWidget.SIZE.width()
											/ 2, to.y)//
							.setDepth(UiDepth.POPUP.value);
					anim.delay(AnimationConstants.FOSSIL_MIGRATION_TIME)//
							.then()//
							.action(new Runnable() {
								@Override
								public void run() {
									layer.add(fameStarPopup.layer());
									fameStarPopup.animate(anim);
								}
							});

					anim.tweenTranslation(animatedLayer)
							.from(from)
							.to(to)
							.in(AnimationConstants.FOSSIL_MIGRATION_TIME)
							.easeInOut()
							.then()
							.delay(AnimationConstants.FOSSIL_WIDGET_FADEIN_TIME)//
							.then()//
							.tweenAlpha(animatedLayer).to(0f)//
							.in(AnimationConstants.FOSSIL_WIDGET_FADEIN_TIME)//
							.easeOut()//
							.then()//
							.action(new Runnable() {
								@Override
								public void run() {
									layer.remove(animatedLayer);
								}
							});

					return null;
				}
			};

			@Override
			public void handle(GameEvent event) {
				event.accept(bounceAnimator);
			}
		});
	}

	@Override
	public void update(int delta) {
		super.update(delta);
		game.eventBus.update();
	}

	public UpkeepDialog showUpkeepDialog() {
		UpkeepDialog dialog = UpkeepDialog.create();
		dialog.show();
		return dialog;
	}

	private void connectForGameOver() {
		Game.currentGame().onGameOver().connect(new Slot<Player>() {
			@Override
			public void onEmit(Player winner) {
				GameOverWidget gameOverWidget = GameOverWidget.create(winner);
				layer.addAt(gameOverWidget.layer(), 193.5f, 78);
				gameOverWidget.layer().setDepth(UiDepth.POPUP.value);
				connectOnRestartGame(gameOverWidget);
			}

			private void connectOnRestartGame(GameOverWidget gameOverWidget) {
				gameOverWidget.onRestartGame().connect(new UnitSlot() {
					@Override
					public void onEmit() {
						PlayingScreen playingScreen = PlayingScreen
								.create(screenStack);
						screenStack.replace(playingScreen, new FadeTransition(
								screenStack));
					}
				});
			}
		});
	}

	private void setToHudLayer(Layer... layers) {
		for (Layer layer : layers) {
			layer.setDepth(UiDepth.HUD.value);
		}
	}

	private void initBackground() {
		ImageLayer bgLayer = graphics().createImageLayer(
				GameImage.BACKGROUND.image);
		layer.add(bgLayer);
	}

	private void initTPUI() {
		GroupLayer.Clipped hudLayer = graphics().createGroupLayer(
				graphics().width(), graphics().height());
		layer.add(hudLayer);
		hudLayer.setDepth(UiDepth.HUD.value);

		root = iface.createRoot(new AbsoluteLayout(),
				BoneWarsStylesheetFactory.sheet(), hudLayer)//
				.setSize(graphics().width(), graphics().height());
		FundsLabel marshFundsLabel = FundsLabel
				.createFor(Game.currentGame().marsh);
		FundsLabel copeFundsLabel = FundsLabel
				.createFor(Game.currentGame().cope);
		FameLabel marshFameLabel = FameLabel
				.createFor(Game.currentGame().marsh);
		FameLabel copeFameLabel = FameLabel.createFor(Game.currentGame().cope);

		root.add(AbsoluteLayout.at(marshFundsLabel, 142, 52));
		root.add(AbsoluteLayout.at(copeFundsLabel, 670, 53));
		root.add(AbsoluteLayout.at(marshFameLabel, 138, 16));
		root.add(AbsoluteLayout.at(copeFameLabel, 666, 16));
	}

	private void initHelpButton() {
		HelpButton helpButton = new HelpButton();
		float x = mapPanel.layer().tx() + mapPanel.layer().width()
				- HelpButton.SIZE.width * 1.5f;
		float y = mapPanel.layer().ty() + HelpButton.SIZE.height;
		root.add(AbsoluteLayout.at(helpButton, x, y, HelpButton.SIZE.width,
				HelpButton.SIZE.height));

		helpButton.onClick(new Slot<Button>() {
			@Override
			public void onEmit(Button event) {
				screenStack.replace(new OptionsScreen(screenStack,
						PlayingScreen.this), screenStack.slide());
			}
		});
	}

	@Override
	public void wasHidden() {
		pointer().setListener(null);
	}

	@Override
	public void wasShown() {
		pointer().setListener(pInput.plistener);
	}

}
