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
import react.UnitSlot;
import tripleplay.util.Input.Registration;
import edu.bsu.bonewars.core.model.Game;
import edu.bsu.bonewars.core.model.Worker;
import edu.bsu.bonewars.core.util.SingleSelectionModel;

public class CopeWorkerWidget implements IWorkerWidget{
	public static final IDimension SIZE = new Dimension(50, 80);

	public static CopeWorkerWidget createFor(Worker worker) {
		return new CopeWorkerWidget(worker);
	}

	private final GroupLayer.Clipped layer;
	private final ImageLayer imageLayer;
	private final Worker worker;
	private Registration registration;
	private ImageLayer pulseAnimationImageLayer;

	private CopeWorkerWidget(final Worker worker) {
		this.worker = checkNotNull(worker);
		checkState(worker.isBigWorker());
		checkState(worker.boss().isCope());
		this.layer = graphics().createGroupLayer(SIZE.width(), SIZE.height());
		initPulsingLayer();
		Image image = GameImage.COPE_WORKER.image;
		imageLayer = graphics().createImageLayer(image);

		this.layer.add(imageLayer);
		
		worker.isSelectable().connect(new Slot<Boolean>(){
			
			@Override
			public void onEmit(Boolean event) {
				if(event){
					registration = screen().pInput.register(layer, new Pointer.Adapter() {
						@Override
						public void onPointerStart(Event event) {
							toggle();
						}
					});
				}else{
					if(registration != null){
						registration.cancel();
					}
				}
				
			}
		});

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
	
	private void initPulsingLayer(){
		pulseAnimationImageLayer = WorkerPulsingLayer.createForWorkerWidget(this).pulseAnimationImageLayer();
		layer.add(pulseAnimationImageLayer);
	}
	
	public Layer.HasSize layer() {
		return layer;
	}

	private void toggle() {
		if (worker.isSelected()) {
			switchToUnselectedImage();
			worker.deselect();
		} else {
			GameSfx.COPE_WORKER.play();
			switchToSelectedImage();
			worker.select();
		}
	}

	private void switchToUnselectedImage() {
		imageLayer.setImage(GameImage.COPE_WORKER.image);
	}

	private void switchToSelectedImage() {
		imageLayer.setImage(GameImage.COPE_WORKER_SELECTED.image);
	}

	private void switchToUnavailableImage() {
		imageLayer.setImage(GameImage.COPE_WORKER_WORKED.image);
	}

	@Override
	public Worker worker() {
		return worker;
	}

	@Override
	public IDimension size() {
		return SIZE;
	}
}
