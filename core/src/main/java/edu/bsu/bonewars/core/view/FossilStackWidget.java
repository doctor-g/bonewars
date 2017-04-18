package edu.bsu.bonewars.core.view;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static edu.bsu.bonewars.core.PlayingScreen.screen;
import static playn.core.PlayN.graphics;
import playn.core.GroupLayer;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.Layer;
import playn.core.Pointer;
import playn.core.Pointer.Event;
import pythagoras.i.Dimension;
import pythagoras.i.IDimension;
import react.Slot;
import react.ValueView.Listener;
import tripleplay.util.Input.Registration;
import edu.bsu.bonewars.core.PlayingScreen;
import edu.bsu.bonewars.core.actions.AnalyzeAction;
import edu.bsu.bonewars.core.actions.AnalyzedPublishAction;
import edu.bsu.bonewars.core.actions.UnanalyzedPublishedAction;
import edu.bsu.bonewars.core.model.Fossil;
import edu.bsu.bonewars.core.model.Fossil.Quality;
import edu.bsu.bonewars.core.model.FossilStack;
import edu.bsu.bonewars.core.model.Game;

public class FossilStackWidget {
	public static final IDimension SIZE = new Dimension(96, 50);
	public static final IDimension FOSSIL_ICON_MARGIN = new Dimension(6, 6);

	public static FossilStackWidget createFor(FossilStack stack) {
		return new FossilStackWidget(stack);
	}

	private Registration analyzeInputRegistration;
	private Registration publishInputRegistration;
	private final GroupLayer.Clipped layer;
	private final Layer.HasSize analyzeActionFlag;
	private Layer.HasSize publishActionFlag;
	private AnalyzeAction analyzeAction;
	private AnalyzedPublishAction analyzedPublishAction;
	private UnanalyzedPublishedAction unanalyzedPublishAction;
	private ImageLayer questionMark;
	private ImageLayer fossilTypeImage;
	private ImageLayer completenessImage;
	private final FossilStack stack;

	private FossilStackWidget(final FossilStack stack) {
		this.stack = checkNotNull(stack);
		this.layer = graphics().createGroupLayer(SIZE.width(), SIZE.height());
		this.layer.setDepth(UiDepth.FOSSIL_STACK.value);

		fillBackground();

		initPublishFlag();
		analyzeActionFlag = graphics().createImageLayer(
				GameImage.ANALYZE_FLAG.image);
		initFossilTypeImages();
		initCompletenessImages();
		pullActionsFromRegistry();
		initQuestionMarkImage();
		connectAnalyzedPublish();
		connectNumberOfUnanalyzedFossilsSignal();
		connectAnalyzeActionFlag();
		connectToBestFossil();
		connectUnanalyzedPublish();
		layer.setVisible(false);
		connectVisibilityToNumberOfFossils();
	}

	private void pullActionsFromRegistry() {
		analyzedPublishAction = Game.currentGame().actionRegistry()
				.getAnalyzedPublishActionForFossilStack(stack);
		unanalyzedPublishAction = Game.currentGame().actionRegistry()
				.getUnanalyzedPublishActionForFossilStack(stack);
		analyzeAction = Game.currentGame().actionRegistry()
				.getAnalyzeActionForFossilStack(stack);
	}

	private void fillBackground() {
		Layer bg = graphics().createImageLayer(
				GameImage.FOSSIL_WIDGET_BACKGROUND.image);
		layer.add(bg);
	}

	private void initPublishFlag() {
		publishActionFlag = graphics().createImageLayer(
				GameImage.PUBLISH_FLAG.image);
		publishActionFlag.setDepth(UiDepth.FOSSIL_STACK_POPUNDER.value);
	}

	private void initFossilTypeImages() {
		Image image = GameImage.getGameImageForFossilType(stack.type());
		fossilTypeImage = graphics().createImageLayer(image);
		setVisabilityOfFossilTypeFlag();
		this.layer.addAt(fossilTypeImage, FOSSIL_ICON_MARGIN.width(),
				FOSSIL_ICON_MARGIN.height());
	}

	private void initCompletenessImages() {
		completenessImage = createArbitraryImageSoThatCompletenessImageIsCorrectSize();
		completenessImage.setVisible(false);
		this.layer.addAt(completenessImage, fossilTypeImage.width() + 6, 4);
	}

	private ImageLayer createArbitraryImageSoThatCompletenessImageIsCorrectSize() {
		return graphics().createImageLayer(
				GameImage.completenessFor(Quality.VERY_LOW));
	}

	private void initQuestionMarkImage() {
		questionMark = graphics().createImageLayer(
				GameImage.questionMarksFor(stack.numberOfUnanalyzedFossils()
						.get()));
		this.layer.addAt(questionMark, fossilTypeImage.width()
				+ completenessImage.width() + 12, 6);
	}

	private void connectAnalyzedPublish() {
		analyzedPublishAction.available().connect(new Listener<Boolean>() {
			@Override
			public void onChange(Boolean value, Boolean oldValue) {
				if (value) {
					popupPublishActionFlag();
				} else {
					removePublishActionFlag();
				}
			}
		});
	}

	private void connectUnanalyzedPublish() {
		unanalyzedPublishAction.available().connect(new Listener<Boolean>() {
			@Override
			public void onChange(Boolean value, Boolean oldValue) {
				if (value) {
					popupPublishActionFlag();
				} else {
					removePublishActionFlag();
				}
			}
		});
	}

	private void popupPublishActionFlag() {
		final float rightSide = rightSide();
		publishActionFlag.setTranslation(rightSide - publishActionFlag.width(),
				popupFlagHeight());
		layer.parent().add(publishActionFlag);
		PlayingScreen.screen().anim//
				.tweenX(publishActionFlag)//
				.to(rightSide)//
				.in(AnimationConstants.FLAG_POPOUT_TIME)//
				.easeIn()//
				.then()//
				.action(publishActionEnabler);
	}

	private final float popupFlagHeight() {
		return layer.ty() + 1;
	}

	private final float rightSide() {
		return layer.tx() + layer.width();
	}

	private final Runnable publishActionEnabler = new Runnable() {
		@Override
		public void run() {
			publishInputRegistration = screen().pInput.register(
					publishActionFlag, new Pointer.Adapter() {
						@Override
						public void onPointerEnd(Event event) {
							removePublishActionFlag();

							if (isInteractionAmbiguous()) {
								showAmbiguousPublishDialog();
							} else {
								GameSfx.PUBLISH.play();
								if (analyzedPublishAction.isAvailable()) {
									analyzedPublishAction.doAction();
								} else {
									checkState(unanalyzedPublishAction
											.isAvailable());
									unanalyzedPublishAction.doAction();
								}
							}
						}

						private boolean isInteractionAmbiguous() {
							return unanalyzedPublishAction.isAvailable()
									&& analyzedPublishAction.isAvailable();
						}
					});
		}
	};

	private void showAmbiguousPublishDialog() {
		AmbiguousPublishDialog dialog = AmbiguousPublishDialog.create(
				analyzedPublishAction, unanalyzedPublishAction);
		dialog.show();
	}

	private final Runnable publishActionFlagRemover = new Runnable() {
		public void run() {
			if (publishActionFlag.parent() != null) {
				publishActionFlag.parent().remove(publishActionFlag);
			}
		}
	};

	private void removePublishActionFlag() {
		if (publishInputRegistration != null) {
			publishInputRegistration.cancel();
			publishInputRegistration = null;
			if (!stack.isEmpty()) {
				animatePublishActionFlagBack();
			}
		}
	}

	private void animatePublishActionFlagBack() {
		PlayingScreen.screen().anim//
				.tweenX(publishActionFlag)//
				.to(rightSide() - publishActionFlag.width())//
				.in(AnimationConstants.FLAG_POPOUT_TIME)//
				.easeIn().then().action(publishActionFlagRemover);
	}

	private void connectNumberOfUnanalyzedFossilsSignal() {
		stack.numberOfUnanalyzedFossils().connect(new Listener<Integer>() {
			@Override
			public void onChange(Integer value, Integer oldValue) {
				questionMark.setImage(GameImage.questionMarksFor(value));
				handleCompletenessImage();
			}
		});
	}

	private void setVisabilityOfFossilTypeFlag() {
		fossilTypeImage.setVisible(!stack.isEmpty());
	}

	private void connectAnalyzeActionFlag() {
		analyzeAction.available().connect(new Listener<Boolean>() {
			@Override
			public void onChange(Boolean value, Boolean oldValue) {
				if (value) {
					popupAnalyzeActionFlag();
				} else {
					removeAnalyzePopup();
				}
			}
		});
	}

	private void popupAnalyzeActionFlag() {
		analyzeActionFlag.setTranslation(layer.tx(), popupFlagHeight());
		layer.parent().add(analyzeActionFlag);

		PlayingScreen.screen().anim//
				.tweenX(analyzeActionFlag)//
				.to(layer.tx() - analyzeActionFlag.width())//
				.in(AnimationConstants.FLAG_POPOUT_TIME)//
				.easeIn()//
				.then()//
				.action(analyzeActionEnabler);
	}

	private final Runnable analyzeActionEnabler = new Runnable() {
		public void run() {
			analyzeInputRegistration = screen().pInput.register(
					analyzeActionFlag, new Pointer.Adapter() {
						@Override
						public void onPointerStart(Event event) {
							GameSfx.ANALYZE.play();
							analyzeAction.doAction();
						}
					});
		}
	};

	private final Runnable analyzeFlagRemover = new Runnable() {
		public void run() {
			if (analyzeActionFlag.parent() != null)
				analyzeActionFlag.parent().remove(analyzeActionFlag);
		}
	};

	private void removeAnalyzePopup() {
		analyzeInputRegistration.cancel();
		analyzeInputRegistration = null;
		if (!stack.isEmpty()) {
			animateAnalyzeFlagBack();
		}
	}

	private void animateAnalyzeFlagBack() {
		PlayingScreen.screen().anim//
				.tweenX(analyzeActionFlag)//
				.to(layer.tx())//
				.in(AnimationConstants.FLAG_POPOUT_TIME)//
				.easeIn()//
				.then()//
				.action(analyzeFlagRemover);
	}

	private void connectToBestFossil() {
		stack.getBestFossil().connect(new Listener<Fossil>() {
			@Override
			public void onChange(Fossil fossil, Fossil oldValue) {
				setVisabilityOfFossilTypeFlag();
				handleCompletenessImage();
			}
		});
	}

	private void handleCompletenessImage() {
		Fossil bestFossil = stack.getBestFossil().get();
		if (bestFossil == null) {
			completenessImage.setVisible(false);
			return;
		}
		if (bestFossil.isAnalyzed().get()) {
			completenessImage.setVisible(true);
			completenessImage.setImage(GameImage.completenessFor(bestFossil
					.quality()));
		} else {
			completenessImage.setVisible(false);
		}
	}

	private void connectVisibilityToNumberOfFossils() {
		stack.numberOfFossils().connect(new Slot<Integer>() {
			@Override
			public void onEmit(final Integer numberOfFossils) {
				if (numberOfFossils == 0) {
					layer.setVisible(false);
					publishActionFlagRemover.run();
					analyzeFlagRemover.run();
				} else {
					PlayingScreen.screen().anim//
							.delay(AnimationConstants.FOSSIL_MIGRATION_TIME)//
							.then()//
							.action(new Runnable() {
								@Override
								public void run() {
									if (numberOfFossils > 0) {
										layer.setVisible(true);

										// This is a bit tricky. We run the
										// animation every time, *even* if the
										// widget is already shown. The reason
										// this works is that we only set the
										// alpha to zero here, so the other
										// tweens are going from 1 to 1, which
										// of course shows no visible effect.
										layer.setAlpha(0);
									}
								}
							})//
							.then()//
							.tweenAlpha(layer)//
							.from(layer.alpha())//
							.to(1)//
							.easeIn()//
							.in(AnimationConstants.FOSSIL_WIDGET_FADEIN_TIME);
				}
			}
		});
	}

	public Layer.HasSize layer() {
		return layer;
	}

}
