package edu.bsu.bonewars.core.view;

import static playn.core.PlayN.graphics;
import playn.core.Font;
import playn.core.Font.Style;

public enum GameFont {

	REGULAR("Arapey", "fonts/arapey-regular.ttf");

	public final String name;
	public final String path;

	private GameFont(String name, String path) {
		this.name = name;
		this.path = path;
	}

	@Override
	public String toString() {
		return name;
	}

	public Font atSize(float size) {
		// According to the docs, you should only use PLAIN, because only Java
		// platform supports changing styles programmatically, and it does so
		// poorly. If we need different kinds of a font (bold, italic, etc.),
		// then we should register them separately.
		return graphics().createFont(name, Style.PLAIN, size);
	}
}
