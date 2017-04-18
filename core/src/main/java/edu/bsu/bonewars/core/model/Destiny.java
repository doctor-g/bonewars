package edu.bsu.bonewars.core.model;

import react.SignalView;

public interface Destiny {

	public SignalView<Destiny> onComplete();

	public void run(Game game);

}
