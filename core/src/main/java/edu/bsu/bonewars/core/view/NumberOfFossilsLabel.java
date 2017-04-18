package edu.bsu.bonewars.core.view;

import static com.google.common.base.Preconditions.checkNotNull;
import pythagoras.i.Dimension;
import pythagoras.i.IDimension;
import react.ValueView.Listener;
import tripleplay.ui.Label;
import edu.bsu.bonewars.core.model.Site;

public class NumberOfFossilsLabel extends Label{
	private static final IDimension SIZE = new Dimension(33, 80);

	public static NumberOfFossilsLabel createFor(Site site) {
		return new NumberOfFossilsLabel(site);
	}

	private NumberOfFossilsLabel(Site site) {
		checkNotNull(site);
		setSize(SIZE.width(), SIZE.height());
		updateText(site.numberOfFossils().get());
		site.numberOfFossils().connect(new Listener<Integer>() {
			@Override
			public void onChange(Integer value, Integer oldValue) {
				updateText(value);
			}
		});
	}

	private void updateText(int numberOfFossils) {
		text.update(""+numberOfFossils);
	}
}
