package edu.bsu.bonewars.core.view;

import static playn.core.PlayN.graphics;
import playn.core.GroupLayer;
import playn.core.Layer;
import pythagoras.i.Dimension;
import pythagoras.i.IDimension;
import react.Slot;
import edu.bsu.bonewars.core.model.Game;
import edu.bsu.bonewars.core.model.Player;
import edu.bsu.bonewars.core.model.Worker;

public final class MarshWorkerPanel {

	private final GroupLayer.Clipped layer;

	private static final IDimension SIZE = new Dimension(379 - 184, 72);
	private static final int WORKER_SPACING = 6; // made this number up

	public MarshWorkerPanel() {
		Player player = Game.currentGame().marsh;
		this.layer = graphics().createGroupLayer(SIZE.width(), SIZE.height());
		MarshWorkerWidget marshWorker = MarshWorkerWidget.createFor(player
				.bigWorker());
		layer.add(marshWorker.layer());
		addLittleWorkersIcons(player);

		player.onLittleWorkerCollectionChange().connect(new Slot<Player>() {
			@Override
			public void onEmit(Player aPlayer) {
				layer.removeAll();
				layer.add(MarshWorkerWidget.createFor(aPlayer.bigWorker())
						.layer());
				addLittleWorkersIcons(aPlayer);
			}
		});
	}

	private void addLittleWorkersIcons(Player player) {
		int i = 0;
		for (Worker worker : player.littleWorkerCollection()) {
			addWorkerIcon(worker, i);
			i++;
		}
	}

	private void addWorkerIcon(Worker worker, int i) {
		LittleWorkerWidget widget = LittleWorkerWidget.createFor(worker);
		int spacing = WORKER_SPACING;
		layer.addAt(widget.layer(),
				(i * (LittleWorkerWidget.SIZE.width() + spacing)) + 53, 35);
	}

	public Layer.HasSize layer() {
		return layer;
	}
}
