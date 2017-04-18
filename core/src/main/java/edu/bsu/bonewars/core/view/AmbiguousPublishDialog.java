package edu.bsu.bonewars.core.view;

import static playn.core.PlayN.graphics;
import playn.core.Color;
import playn.core.Font;
import playn.core.GroupLayer;
import playn.core.Layer;
import pythagoras.i.Dimension;
import react.Slot;
import tripleplay.ui.Background;
import tripleplay.ui.Button;
import tripleplay.ui.Constraints;
import tripleplay.ui.Group;
import tripleplay.ui.Icons;
import tripleplay.ui.Label;
import tripleplay.ui.Root;
import tripleplay.ui.Style;
import tripleplay.ui.Style.HAlign;
import tripleplay.ui.layout.AxisLayout;
import edu.bsu.bonewars.core.PlayingScreen;
import edu.bsu.bonewars.core.actions.AnalyzedPublishAction;
import edu.bsu.bonewars.core.actions.UnanalyzedPublishedAction;

public final class AmbiguousPublishDialog extends AbstractDialog {

	private static final Dimension SIZE = new Dimension(400, 300);
	public static final int GOLD_BORDER_COLOR = Color.rgb(203, 182, 115);

	private static final String MESSAGE = "Which do you publish?";
	private static final float MESSAGE_FONT_SIZE = 24f;
	public static final float BUTTON_FONT_SIZE = 20f;
	public static Background buttonBackground = createButtonBackgroundAsInTriplePlaySimpleStyles();
	public static Font buttonFont = GameFont.REGULAR.atSize(BUTTON_FONT_SIZE);
	private AnalyzedPublishAction analyzedPublishAction;
	private UnanalyzedPublishedAction unanalyzedPublishAction;

	public static AmbiguousPublishDialog create(
			AnalyzedPublishAction analyzedPublishAction,
			UnanalyzedPublishedAction unanalyzedPublishAction) {
		return new AmbiguousPublishDialog(analyzedPublishAction,
				unanalyzedPublishAction);
	}

	private final GroupLayer.Clipped layer;

	private AmbiguousPublishDialog(final AnalyzedPublishAction analyzedPublish,
			final UnanalyzedPublishedAction unanalyzedPublish) {
		this.analyzedPublishAction = analyzedPublish;
		this.unanalyzedPublishAction = unanalyzedPublish;
		layer = graphics().createGroupLayer(SIZE.width(), SIZE.height());
		layer.setDepth(UiDepth.DIALOG.value);

		initInterface();
	}

	private void initInterface() {
		Root root = PlayingScreen.screen().iface
				.createRoot(AxisLayout.vertical(),
						BoneWarsStylesheetFactory.sheet(), layer)//
				.setSize(layer.width(), layer.height())
				.addStyles(
						Style.BACKGROUND.is(Background
								.image(GameImage.DIALOG_BG.image)));
		Label label = new Label(MESSAGE)//
				.setStyles(
						Style.FONT.is(GameFont.REGULAR
								.atSize(MESSAGE_FONT_SIZE)),//
						Style.TEXT_WRAP.is(true),//
						Style.HALIGN.is(HAlign.CENTER))//
				.setConstraint(Constraints.maxWidth(SIZE.width() * 0.80f));

		root.add(label);

		Background buttonBackground = createButtonBackgroundAsInTriplePlaySimpleStyles();

		Button publishAnalyzed = new Button("Best Analyzed Fossil",
				Icons.image(GameImage.completenessFor(analyzedPublishAction
						.fossilStack().getBestFossil().get().quality())))
				.addStyles(Style.FONT.is(buttonFont),
						Style.BACKGROUND.is(buttonBackground),
						Style.ICON_POS.above);
		Button publishUnanalyzed = new Button("An Unanalyzed fossil",
				Icons.image(GameImage.questionMarksFor(unanalyzedPublishAction
						.fossilStack().numberOfUnanalyzedFossils().get())))
				.setStyles(Style.FONT.is(buttonFont),
						Style.BACKGROUND.is(buttonBackground),
						Style.ICON_POS.above);
		Group buttonGroup = new Group(AxisLayout.horizontal());
		buttonGroup.add(publishAnalyzed, publishUnanalyzed);
		root.add(buttonGroup);

		publishAnalyzed.onClick(new Slot<Button>() {
			@Override
			public void onEmit(Button event) {
				GameSfx.PUBLISH.play();
				analyzedPublishAction.doAction();
				layer.destroy();
				onClose.emit();
			}
		});
		publishUnanalyzed.onClick(new Slot<Button>() {
			@Override
			public void onEmit(Button event) {
				GameSfx.PUBLISH.play();
				unanalyzedPublishAction.doAction();
				layer.destroy();
				onClose.emit();
			}
		});
	}

	private static Background createButtonBackgroundAsInTriplePlaySimpleStyles() {
		int bgColor = GOLD_BORDER_COLOR, //
		ulColor = 0xFFEEEEEE;
		Background butBg = Background.roundRect(bgColor, 5, ulColor, 2).inset(
				5, 6, 2, 6);
		return butBg;
	}

	@Override
	public Layer.HasSize layer() {
		return layer;
	}

}
