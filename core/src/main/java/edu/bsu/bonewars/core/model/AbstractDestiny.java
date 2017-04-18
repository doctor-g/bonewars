package edu.bsu.bonewars.core.model;

import react.Signal;
import react.SignalView;

public abstract class AbstractDestiny implements Destiny {

	protected Signal<Destiny> onComplete = Signal.create();

	@Override
	public SignalView<Destiny> onComplete() {
		return onComplete;
	}

}
