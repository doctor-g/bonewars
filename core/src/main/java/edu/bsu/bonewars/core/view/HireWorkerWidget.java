package edu.bsu.bonewars.core.view;

import static edu.bsu.bonewars.core.PlayingScreen.screen;
import static playn.core.PlayN.graphics;
import playn.core.GroupLayer;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.Layer;
import playn.core.Pointer;
import playn.core.Pointer.Event;
import react.ValueView.Listener;
import edu.bsu.bonewars.core.actions.HireWorkerAction;
import edu.bsu.bonewars.core.model.Game;

public class HireWorkerWidget {

	private final GroupLayer.Clipped layer;
	private final ImageLayer imageLayer;
	private HireWorkerAction marshHireWorkerAction;
	private HireWorkerAction copeHireWorkerAction;

	public HireWorkerWidget() {
		Image image = GameImage.HIRE_WORKER_FLAG.image;
		imageLayer = graphics().createImageLayer(image);

		this.layer = graphics().createGroupLayer(imageLayer.width(),
				imageLayer.height());
		this.layer.setDepth(UiDepth.HUD.value);
		this.layer.add(imageLayer);
		imageLayer.setVisible(false);

		marshHireWorkerAction = Game.currentGame().actionRegistry()
				.getHireWorkerAction(Game.currentGame().marsh);
		copeHireWorkerAction = Game.currentGame().actionRegistry()
				.getHireWorkerAction(Game.currentGame().cope);
		Listener<Boolean> hireWorkerListener = new Listener<Boolean>() {

			@Override
			public void onChange(Boolean value, Boolean oldValue) {
				imageLayer.setVisible(value);
				layer.setVisible(value);
			}

		};
		marshHireWorkerAction.available().connect(hireWorkerListener);
		copeHireWorkerAction.available().connect(hireWorkerListener);

		screen().pInput.register(layer, new Pointer.Adapter() {
			@Override
			public void onPointerStart(Event event) {
				if (Game.currentGame().currentPlayer().get().isCope())
					copeHireWorkerAction.doAction();
				else {
					marshHireWorkerAction.doAction();
				}
				GameSfx.ADD_WORKER.play();
			}
		});

	}

	public Layer.HasSize layer() {
		return layer;
	}

}