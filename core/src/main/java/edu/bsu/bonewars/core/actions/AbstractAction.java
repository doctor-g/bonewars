package edu.bsu.bonewars.core.actions;

import edu.bsu.bonewars.core.model.Action;

public abstract class AbstractAction implements Action {

	@Override
	public boolean isAvailable() {
		return available().get();
	}

}
