package edu.bsu.bonewars.core;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static playn.core.PlayN.graphics;
import playn.core.Font;
import pythagoras.f.Dimension;
import react.Slot;
import react.Value;
import react.ValueView.Listener;
import tripleplay.game.Screen;
import tripleplay.game.ScreenStack;
import tripleplay.game.UIScreen;
import tripleplay.ui.Background;
import tripleplay.ui.Button;
import tripleplay.ui.CheckBox;
import tripleplay.ui.Constraints;
import tripleplay.ui.Element;
import tripleplay.ui.Group;
import tripleplay.ui.Icons;
import tripleplay.ui.Label;
import tripleplay.ui.Root;
import tripleplay.ui.Shim;
import tripleplay.ui.SimpleStyles;
import tripleplay.ui.Style;
import tripleplay.ui.Styles;
import tripleplay.ui.Stylesheet;
import tripleplay.ui.Tabs;
import tripleplay.ui.TextWidget;
import tripleplay.ui.layout.AxisLayout;
import tripleplay.ui.layout.TableLayout;
import edu.bsu.bonewars.core.actions.RaiseFundsAction;
import edu.bsu.bonewars.core.view.AudioSystem;
import edu.bsu.bonewars.core.view.GameFont;
import edu.bsu.bonewars.core.view.GameImage;
import edu.bsu.bonewars.core.view.Palette;

public class OptionsScreen extends UIScreen {
	private static final String[] TEAM = { "Andrew Baumann",
			"Charlie Ecenbarger", "Jordan Hale", "Paisley Hansen",
			"Joshua Kattner", "Austin Pensinger", "Scott Schapker",
			"Andy Thompson", "Adam Wallace", "You Wu", "Ethan Burnsides Yazel" };
	private static final Dimension CONTENT_SIZE = new Dimension(550, 315);

	private final ScreenStack screenStack;
	private final Screen toReturnTo;

	public OptionsScreen(ScreenStack screenStack, Screen toReturnTo) {
		this.screenStack = checkNotNull(screenStack);
		this.toReturnTo = checkNotNull(toReturnTo);

		Root root = iface.createRoot(//
				AxisLayout.vertical(), //
				optionsScreenStyleSheet(),//
				layer)//
				.setSize(graphics().width(), graphics().height());
		root.addStyles(Style.BACKGROUND.is(Background
				.image(GameImage.OPTIONS_BACKGROUND.image)));

		Tabs tabs = new Tabs();
		tabs.add("About", asTabContent(makeAboutPanel()));
		tabs.add("How to Play", asTabContent(makeHowToPlayPanel()));
		tabs.add("Audio", asTabContent(makeSoundPanel()));
		tabs.add("Credits", asTabContent(makeCreditsPanel()));

		root.add(
				new Shim(0f, 150f),
				new Group(AxisLayout.vertical())//
						.addStyles(
								Style.BACKGROUND.is(Background.roundRect(
										Palette.WOOD_YELLOW, 6f,
										Palette.EARTHY_BROWN, 2f)))//
						.add(new Shim(0f, 6f),//
								tabs,//
								new Shim(0f, 6f)), //
				new Shim(0f, 6f),//
				makeCloseButton());
	}

	private Stylesheet optionsScreenStyleSheet() {
		Font aFont = GameFont.REGULAR.atSize(20f);
		return SimpleStyles.newSheetBuilder()
				.add(TextWidget.class, Style.FONT.is(aFont))//
				.create();
	}

	private Element<?> asTabContent(Element<?> group) {
		return group.setConstraint(Constraints.fixedSize(CONTENT_SIZE.width,
				CONTENT_SIZE.height));
	}

	private Group makeAboutPanel() {
		String text = "The year is 1877. Othniel Charles Marsh and Edward Drinker Cope are engaged in a bitter rivalry to become the most famous paleontologist.\n\nThe player on the left is Marsh, and the player on the right is Cope. Acquire sites, hire workers, excavate and analyze fossils, and publish your results to earn fame.\n\nMay the best paleontologist win!";
		return new Group(AxisLayout.vertical())
				//
				.add(new Label(text)//
						.addStyles(Style.TEXT_WRAP.on, Style.HALIGN.left)//
						.setConstraint(
								Constraints
										.fixedWidth(CONTENT_SIZE.width * 0.8f)));
	}

	private Group makeSoundPanel() {
		return new Group(new TableLayout(new TableLayout.Column[] //
				{ TableLayout.COL.alignRight(), TableLayout.COL.alignLeft() }))//
				.add(new ValueCheckBox(AudioSystem.instance().musicMute),
						new Label("Mute music"),
						new ValueCheckBox(AudioSystem.instance().sfxMute),
						new Label("Mute sound effects"));
	}

	private Group makeCreditsPanel() {
		Font biggerFont = GameFont.REGULAR.atSize(20f);
		Styles headerStyle = Styles.make(Style.FONT.is(biggerFont),//
				Style.TEXT_WRAP.on);
		Font smallerFont = GameFont.REGULAR.atSize(16f);
		Styles studentStyle = Styles.make(Style.FONT.is(smallerFont));

		Group credits = new Group(AxisLayout.vertical())
				.add(new Label(
						"Made by a multidisciplinary team of students\nat Ball State University")//
						.addStyles(headerStyle));

		checkState(TEAM.length % 2 == 1);
		Group twoCol = new Group(new TableLayout(new TableLayout.Column[] {
				TableLayout.COL.alignLeft(), //
				TableLayout.COL.alignLeft() //
				}).gaps(0, 24));
		for (int i = 0; i < TEAM.length / 2; i++) {
			twoCol.add(new Label(TEAM[i]).addStyles(studentStyle),//
					new Label(TEAM[i + TEAM.length / 2])
							.addStyles(studentStyle));
		}
		credits.add(twoCol, //
				new Label(TEAM[TEAM.length - 1]).addStyles(studentStyle));

		credits.add(new Shim(0f, 6f),//
				new Label("With guidance from faculty mentor")//
						.addStyles(headerStyle),//
				new Label("Paul Gestwicki").addStyles(studentStyle),//
				new Shim(0f, 6f),//
				new Label("In cooperation with")//
						.addStyles(headerStyle),//
				new Label("The Children's Museum of Indianapolis")
						.addStyles(studentStyle));

		return credits;
	}

	private Group makeHowToPlayPanel() {
		return new Group(AxisLayout.vertical()).add(new Label(
				"Excavate, analyze, and publish fossils to earn Fame!")//
				.addStyles(Style.FONT.is(GameFont.REGULAR.atSize(20f))),//
				new Shim(0f, 12f),//
				new Label("Any worker can take these actions:"),//
				makeLittleWorkerActionTable(),//
				new Shim(0f, 12f),//
				new Label("Only Marsh and Cope can take these actions:"),//
				makeMarshAndCopeActionTable(),//
				new Shim(0f, 12f),//
				new Label("Higher quality fossils are worth more Fame!"));
	}

	private Element<?> makeLittleWorkerActionTable() {
		return new Group(AxisLayout.horizontal())//
				.add(new Label("Excavate", Icons
						.image(GameImage.EXCAVATION_ICON.image)),//
						new Shim(24f, 0f),//
						new Label("Analyze", Icons
								.image(GameImage.ANALYZE_FLAG.image)));
	}

	private Element<?> makeMarshAndCopeActionTable() {
		return new Group(AxisLayout.horizontal())//
				.add(new Group(new TableLayout(new TableLayout.Column[] {
						TableLayout.COL.alignRight(),
						TableLayout.COL.alignLeft() })//
						.gaps(6, 4))//
						.add(new Label(Icons
								.image(GameImage.PUBLISH_FLAG.image)),
								new Label("Publish"), makeFakeRaiseFundsIcon(),
								new Label("Raise Funds")), //
						new Shim(12f, 0f),//
						new Group(new TableLayout(new TableLayout.Column[] {
								TableLayout.COL.alignRight(),
								TableLayout.COL.alignLeft() })//
								.gaps(0, 4)//
						)//
						.add(new Label(Icons.image(GameImage.WORKER.image)),//
								new Label("Hire a Worker"),
								new Label(Icons
										.image(GameImage.ACQUIRE_FLAG.image)),
								new Label("Acquire Site")));
	}

	private Element<?> makeFakeRaiseFundsIcon() {
		return new Label("+$"
				+ RaiseFundsAction.AMOUNT_GAINED_THROUGH_ACTION.asInt())
				.addStyles(Style.BACKGROUND.is(Background.bordered(0,
						Palette.EARTHY_BROWN, 1f)));
	}

	private Button makeCloseButton() {
		return new Button("OK")//
				.onClick(new Slot<Button>() {
					@Override
					public void onEmit(Button event) {
						screenStack.replace(toReturnTo, screenStack.slide()
								.right());
					}
				});
	}

	private class ValueCheckBox extends CheckBox {
		public ValueCheckBox(final Value<Boolean> value) {
			super('\u00d7');
			checked.update(value.get());
			value.connect(new Listener<Boolean>() {
				@Override
				public void onChange(Boolean newValue, Boolean oldValue) {
					checked.update(newValue);
				}
			});
			checked.connect(new Listener<Boolean>() {
				@Override
				public void onChange(Boolean newValue, Boolean oldValue) {
					value.update(newValue);
				}
			});
		}
	}

}
