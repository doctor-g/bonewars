package edu.bsu.bonewars.core.model;

import react.UnitSignal;
import react.ValueView;

public interface Action {
	
	UnitSignal actionCompleteSignal();
	
	void doAction();
	
	ValueView<Boolean> available();
	
	boolean isAvailable();
}
