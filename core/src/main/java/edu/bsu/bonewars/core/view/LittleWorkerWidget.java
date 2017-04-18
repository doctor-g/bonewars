package edu.bsu.bonewars.core.view;

import static com.google.common.base.Preconditions.checkNotNull;
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
import react.UnitSlot;
import tripleplay.util.Input.Registration;
import edu.bsu.bonewars.core.model.Game;
import edu.bsu.bonewars.core.model.Worker;
import edu.bsu.bonewars.core.util.SingleSelectionModel;

public final class LittleWorkerWidget implements IWorkerWidget {

	public static final IDimension SIZE = new Dimension(33, 38);

	public static LittleWorkerWidget createFor(Worker worker) {
		return new LittleWorkerWidget(worker);
	}

	private final GroupLayer.Clipped layer;
	private final ImageLayer imageLayer;
	private final Worker worker;
	private Registration registration;
	private ImageLayer pulseAnimationImageLayer;

	private LittleWorkerWidget(final Worker worker) {
		this.worker = checkNotNull(worker);
		this.layer = graphics().createGroupLayer(SIZE.width(), SIZE.height());
		initPulsingLayer();
		Image image = GameImage.WORKER.image;
		imageLayer = graphics().createImageLayer(image);
		this.layer.add(imageLayer);
		configureStartingImageBasedOnCurrentGameState();
		allowClickingWhenSelectable();
		configureImageUpdateBasedOnWhoIsSelected();
		configureImageUpdateBasedOnBeingReadyToWork();
	}
	
	private void initPulsingLayer(){
		pulseAnimationImageLayer = WorkerPulsingLayer.createForWorkerWidget(this).pulseAnimationImageLayer();
		layer.add(pulseAnimationImageLayer);
	}

	private void configureStartingImageBasedOnCurrentGameState() {
		if (worker.boss().equals(Game.currentGame().currentPlayer().get())) {
			if (worker.isReadyForWork().get()) {
				registration = screen().pInput.register(layer,
						new Pointer.Adapter() {
							@Override
							public void onPointerStart(Event event) {
								toggle();
							}
						});
			}
		}
	}

	private void allowClickingWhenSelectable() {
		worker.isSelectable().connect(new Slot<Boolean>() {
			@Override
			public void onEmit(Boolean event) {
				if (event) {
					registration = screen().pInput.register(layer,
							new Pointer.Adapter() {
								@Override
								public void onPointerStart(Event event) {
									toggle();
								}
							});
				} else {
					if (registration != null) {
						registration.cancel();
					}
				}
			}
		});
	}

	private void configureImageUpdateBasedOnWhoIsSelected() {
		final SingleSelectionModel<Worker> selectionModel = Game.currentGame()
				.workerSelectionModel();
		selectionModel.onChange().connect(new UnitSlot() {
			@Override
			public void onEmit() {
				boolean isReady = worker.isReadyForWork().get();
				if (iAmNotSelected()) {
					updateImageBasedForReadiness(isReady);
				}
			}

			private boolean iAmNotSelected() {
				return someoneElseIsSelected(selectionModel)
						|| !selectionModel.hasSelection();
			}

			private void updateImageBasedForReadiness(boolean isReady) {
				if (isReady) {
					switchToUnselectedImage();
				} else {
					switchToUnavailableImage();
				}
			}

			private boolean someoneElseIsSelected(
					SingleSelectionModel<Worker> selectionModel) {
				return selectionModel.hasSelection()
						&& !selectionModel.selection().equals(worker);
			}
		});
	}

	private void configureImageUpdateBasedOnBeingReadyToWork() {
		worker.isReadyForWork().connect(new Slot<Boolean>() {

			@Override
			public void onEmit(Boolean isReady) {
				if (isReady) {
					switchToUnselectedImage();
				} else {
					switchToUnavailableImage();
				}
			}
		});
	}

	public Layer.HasSize layer() {
		return layer;
	}
	
	public Worker worker(){
		return worker;
	}

	private void toggle() {
		if (worker.isSelected()) {
			switchToUnselectedImage();
			worker.deselect();
		} else {
			GameSfx.randomWorkerSound().play();
			switchToSelectedImage();
			worker.select();
		}
	}

	private void switchToUnselectedImage() {
		imageLayer.setImage(GameImage.WORKER.image);
	}

	private void switchToSelectedImage() {
		imageLayer.setImage(GameImage.WORKER_SELECTED.image);
	}

	private void switchToUnavailableImage() {
		imageLayer.setImage(GameImage.WORKER_WORKED.image);
	}

	@Override
	public IDimension size() {
		return SIZE;
	}
}
