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

public class CopeWorkerPanel {
	private final GroupLayer.Clipped layer;

	private static final IDimension SIZE = new Dimension(379 - 184, 72);

	public CopeWorkerPanel() {
		Player player = Game.currentGame().cope;
		this.layer = graphics().createGroupLayer(SIZE.width(), SIZE.height());
		final CopeWorkerWidget copeWorkerWidget = CopeWorkerWidget
				.createFor(player.bigWorker());
		layer.addAt(copeWorkerWidget.layer(), CopeWorkerPanel.SIZE.width()
				- CopeWorkerWidget.SIZE.width(), 0);
		addLittleWorkersIcons(player);
		player.onLittleWorkerCollectionChange().connect(new Slot<Player>() {
			@Override
			public void onEmit(Player aPlayer) {
				layer.removeAll();
				layer.addAt(
						CopeWorkerWidget.createFor(aPlayer.bigWorker()).layer(),
						CopeWorkerPanel.SIZE.width()
								- CopeWorkerWidget.SIZE.width(), 0);
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

	private void addWorkerIcon(Worker worker, int index) {
		LittleWorkerWidget widget = LittleWorkerWidget.createFor(worker);
		layer.addAt(widget.layer(), calculateWorkerIconLocation(index), 35);
		widget.layer().setDepth(UiDepth.HUD.value);
	}

	private int calculateWorkerIconLocation(int i) {
		int x = SIZE.width() - LittleWorkerWidget.SIZE.width() * (i + 1) - 53;
		return x;
	}

	public Layer.HasSize layer() {
		return layer;
	}
}
