package edu.bsu.bonewars.core.view;

import pythagoras.i.IDimension;
import edu.bsu.bonewars.core.model.Worker;

public interface IWorkerWidget {
	
	Worker worker();
	
	IDimension size();
}
