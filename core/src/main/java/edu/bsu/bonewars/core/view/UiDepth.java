package edu.bsu.bonewars.core.view;

public enum UiDepth {

	HUD(2f), //
	HUD_POPUNDER(1.5f), //
	SITE(2f), //
	POPUP(3f), //
	BOTTOM(0f), //
	SITE_POPUNDER(1.5f), //
	DIALOG(4f), //
	FOSSIL_STACK(2f), //
	FOSSIL_STACK_POPUNDER(1.5f);

	public final float value;

	private UiDepth(float value) {
		this.value = value;
	}
}
