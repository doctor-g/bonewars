package edu.bsu.bonewars.core.view;

import static playn.core.PlayN.pointer;
import playn.core.Layer;
import react.UnitSignal;
import react.UnitSlot;
import edu.bsu.bonewars.core.PlayingScreen;

public abstract class AbstractDialog {

	public abstract Layer.HasSize layer();

	protected final UnitSignal onClose = new UnitSignal();

	private final UnitSlot pointerReconnector = new UnitSlot() {
		@Override
		public void onEmit() {
			onClose.disconnect(this);
			pointer().setListener(PlayingScreen.screen().pInput.plistener);
		}
	};

	protected AbstractDialog() {
		configureLayerRemovalOnClose();
	}

	private void configureLayerRemovalOnClose() {
		onClose.connect(new UnitSlot() {
			@Override
			public void onEmit() {
				if (layer().parent() != null)
					layer().parent().remove(layer());
			}
		});
	}

	public void show() {
		disconnectPointerInput();
		displayDialog();
		onClose.connect(pointerReconnector);
	}

	private void disconnectPointerInput() {
		pointer().setListener(null);
	}

	private void displayDialog() {
		PlayingScreen.screen().layer.add(layer());
		layer().setDepth(UiDepth.DIALOG.value);
		ViewUtils.centerInScreen(layer());
	}

	public AbstractDialog onClose(UnitSlot slot) {
		onClose.connect(slot);
		return this;
	}

}
