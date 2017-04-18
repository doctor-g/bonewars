package edu.bsu.bonewars.core.view;

import static com.google.common.base.Preconditions.checkNotNull;
import static playn.core.PlayN.graphics;
import playn.core.Font;
import playn.core.GroupLayer;
import playn.core.Layer;
import pythagoras.i.Dimension;
import pythagoras.i.IDimension;
import react.Slot;
import react.ValueView.Listener;
import tripleplay.ui.Background;
import tripleplay.ui.Button;
import tripleplay.ui.Group;
import tripleplay.ui.Label;
import tripleplay.ui.Menu;
import tripleplay.ui.MenuHost;
import tripleplay.ui.MenuItem;
import tripleplay.ui.Root;
import tripleplay.ui.Shim;
import tripleplay.ui.Style;
import tripleplay.ui.layout.AxisLayout;
import edu.bsu.bonewars.core.PlayingScreen;
import edu.bsu.bonewars.core.model.Game;
import edu.bsu.bonewars.core.model.Upkeep;

public class UpkeepDialog extends AbstractDialog {

	private static final IDimension SIZE = new Dimension(400, 300);
	private static final int MESSAGE_SIZE = 22;
	private static final Font FONT = GameFont.REGULAR.atSize(MESSAGE_SIZE);

	public static UpkeepDialog create() {
		return new UpkeepDialog();
	}

	private final Game game = Game.currentGame();
	private final GroupLayer.Clipped layer;
	private final Root root;
	private final Upkeep upkeep = Upkeep.create();

	private UpkeepDialog() {
		layer = graphics().createGroupLayer(SIZE.width(), SIZE.height());
		root = PlayingScreen.screen().iface
				.createRoot(AxisLayout.vertical(),
						BoneWarsStylesheetFactory.sheet(), layer)//
				.setSize(layer.width(), layer.height())
				.addStyles(
						Style.BACKGROUND.is(Background
								.image(GameImage.DIALOG_BG.image)));

		root.add(new Label("End of Round " + game.round().get())//
				.addStyles(Style.FONT.is(FONT)), new Label(
				"How many workers do you retain?")//
				.addStyles(Style.FONT.is(FONT)));
		Group mainGroup = new Group(AxisLayout.horizontal());
		root.add(mainGroup);

		final Side marshSide = new Side(upkeep.marsh);
		final Side copeSide = new Side(upkeep.cope);
		mainGroup.add(marshSide, //
				new Shim(12f, 0f), //
				copeSide);

		Button done = new Button("Done");
		done.onClick(new Slot<Button>() {
			@Override
			public void onEmit(Button event) {
				marshSide.updateGameForSelection();
				copeSide.updateGameForSelection();
				onClose.emit();
			}
		});
		root.add(done);
	}

	@Override
	public Layer.HasSize layer() {
		return layer;
	}

	private final class Side extends Group {
		private final Button workersButton;
		private final Upkeep.Options workerUpkeep;

		public Side(final Upkeep.Options workerUpkeep) {
			super(AxisLayout.vertical());
			this.workerUpkeep = checkNotNull(workerUpkeep);

			workerUpkeep.selection().connect(new Listener<Integer>() {
				@Override
				public void onChange(Integer value, Integer oldValue) {
					workersButton.text.update(value.toString());
				}
			});

			add(new Label(workerUpkeep.player.isCope() ? "Cope" : "Marsh")//
					.addStyles(Style.FONT.is(FONT)));

			final MenuHost menuHost = new MenuHost(
					PlayingScreen.screen().iface, root);
			workersButton = new Button(workerUpkeep.selection().get()
					.toString())//
					.onClick(new Slot<Button>() {
						@Override
						public void onEmit(final Button theButton) {
							MenuHost.Pop pop = new MenuHost.Pop(theButton,
									createMenuForWorkers());
							pop.menu.itemTriggered().connect(
									new Slot<MenuItem>() {
										@Override
										public void onEmit(MenuItem item) {
											workerUpkeep.select(Integer
													.valueOf(item.text.get())
													.intValue());
										}
									});
							menuHost.popup(pop);
						}

						private Menu createMenuForWorkers() {
							Menu menu = new Menu(AxisLayout.vertical()
									.offStretch().gap(3));
							for (int i = workerUpkeep.max(); i >= 0; i--) {
								menu.add(new MenuItem(String.valueOf(i)));
							}
							return menu;
						}
					});
			add(workersButton);

			add(new SalaryLabel());
		}

		public void updateGameForSelection() {
			workerUpkeep.fireWorkersBasedOnSelection();
		}

		private class SalaryLabel extends Label {
			public SalaryLabel() {
				addStyles(Style.FONT.is(FONT));
				updateText();
				workerUpkeep.selection().connect(new Slot<Integer>() {
					@Override
					public void onEmit(Integer event) {
						updateText();
					}
				});
			}

			private void updateText() {
				text.update("Salary cost: $"
						+ String.valueOf(workerUpkeep.salaryExpense()));
			}
		}
	}

}
