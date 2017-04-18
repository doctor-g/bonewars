package edu.bsu.bonewars.core.view;

import static playn.core.PlayN.graphics;
import playn.core.CanvasImage;
import playn.core.ImageLayer;
import react.Slot;
import react.UnitSlot;
import tripleplay.util.Colors;
import edu.bsu.bonewars.core.PlayingScreen;
import edu.bsu.bonewars.core.model.Game;
import edu.bsu.bonewars.core.model.Player;
import edu.bsu.bonewars.core.model.Worker;
import edu.bsu.bonewars.core.util.SingleSelectionModel;


public class WorkerPulsingLayer {
	
	public static WorkerPulsingLayer createForWorkerWidget(IWorkerWidget workerWidget){
		return new WorkerPulsingLayer(workerWidget);
	}
	
	private ImageLayer pulseAnimationImageLayer;
	public IWorkerWidget workerWidget;
	
	public WorkerPulsingLayer(IWorkerWidget workerWidget){
		this.workerWidget = workerWidget;
		createPulseAnimationImageLayer();
	}

	private void createPulseAnimationImageLayer() {
		CanvasImage yellowImage = graphics().createImage(workerWidget.size().width(),
				workerWidget.size().height());
		yellowImage.canvas().setFillColor(Colors.YELLOW);
		yellowImage.canvas().fillRoundRect(0, 0, yellowImage.width(),
				yellowImage.height(), yellowImage.width() / 2);
		pulseAnimationImageLayer = graphics().createImageLayer(yellowImage);
		PlayingScreen.screen().anim
				.repeat(pulseAnimationImageLayer)
				//
				.tweenAlpha(pulseAnimationImageLayer).from(0).easeIn().to(0.7f)
				.in(1000).then()
				//
				.tweenAlpha(pulseAnimationImageLayer).from(0.6f).to(0).in(1000)
				.then().delay(500);
		evaluateAndSetPulseAnimationVisability();
		workerWidget.worker().isReadyForWork().connect(new Slot<Boolean>() {
			@Override
			public void onEmit(Boolean isReadyForWork) {
				evaluateAndSetPulseAnimationVisability();
			}
		});

		Game.currentGame().currentPlayer().connect(new Slot<Player>() {
			@Override
			public void onEmit(Player player) {
				evaluateAndSetPulseAnimationVisability();
			}
		});
		final SingleSelectionModel<Worker> singleSelectionModel = Game.currentGame().workerSelectionModel();
		singleSelectionModel.onChange().connect(new UnitSlot(){

			@Override
			public void onEmit() {
				if(singleSelectionModel.hasSelection()){
						pulseAnimationImageLayer.setVisible(singleSelectionModel.selection().equals(workerWidget.worker()));
				}else{
					evaluateAndSetPulseAnimationVisability();
				}
			}
		});
	}
	
	private void evaluateAndSetPulseAnimationVisability() {
		boolean isWorkerHighlighted = workerWidget.worker().boss().equals(
				Game.currentGame().currentPlayer().get())
				&& workerWidget.worker().isReadyForWork().get();
		pulseAnimationImageLayer.setVisible(isWorkerHighlighted);
	}
	
	public ImageLayer pulseAnimationImageLayer(){
		return pulseAnimationImageLayer;
	}
}
