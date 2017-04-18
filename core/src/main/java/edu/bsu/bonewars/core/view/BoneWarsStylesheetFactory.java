package edu.bsu.bonewars.core.view;

import playn.core.Font;
import tripleplay.ui.SimpleStyles;
import tripleplay.ui.Style;
import tripleplay.ui.Stylesheet;
import tripleplay.ui.TextWidget;

public final class BoneWarsStylesheetFactory {

	private static final Stylesheet sheet;
	static {
		Font aFont = GameFont.REGULAR.atSize(28);
		sheet = SimpleStyles.newSheetBuilder()
				.add(TextWidget.class, Style.FONT.is(aFont))//
				.create();
	}

	public static Stylesheet sheet() {
		return sheet;
	}

}
