package edu.bsu.bonewars.core.view;

import static com.google.common.base.Preconditions.checkState;
import static playn.core.PlayN.graphics;
import playn.core.Font;
import playn.core.GroupLayer;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.Layer.HasSize;
import pythagoras.f.IRectangle;
import pythagoras.f.Rectangle;
import pythagoras.i.Dimension;
import pythagoras.i.IDimension;
import react.Slot;
import tripleplay.ui.Button;
import tripleplay.ui.Label;
import tripleplay.ui.Root;
import tripleplay.ui.Style;
import tripleplay.ui.layout.AxisLayout;

import com.google.common.collect.ImmutableMap;

import edu.bsu.bonewars.core.PlayingScreen;
import edu.bsu.bonewars.core.model.StoryEvent;

public class StoryCard extends AbstractDialog {

	private static final IDimension SIZE = new Dimension(600, 450);
	private static final ImmutableMap<StoryEvent, ViewData> VIEW_DATA = ImmutableMap
			.of(StoryEvent.COPE_FAILS_SILVER_MINE, //
					new ViewData(new Rectangle(52, 31, 491, 146),
							GameMusic.SILVER_MINE_STORY_CARD),//
					StoryEvent.MARSH_CHASED_BY_SIOUX, //
					new ViewData(new Rectangle(87, 27, 424, 109),
							GameMusic.INDIAN_ATTACK_STORY_CARD),//
					StoryEvent.FOSSIL_INFLUX_ADD_FUNDS_TO_PLAYERS,//
					new ViewData(new Rectangle(66, 264, 197, 163),
							GameMusic.FOSSIL_INFLUX_STORY_CARD));

	public static StoryCard createFor(StoryEvent event) {
		return new StoryCard(event);
	}

	private final GroupLayer.Clipped layer;

	private StoryCard(final StoryEvent event) {
		layer = graphics().createGroupLayer(SIZE.width(), SIZE.height());
		Image image = lookupGameImageWithSameNameAs(event);
		ImageLayer imageLayer = graphics().createImageLayer(image);
		layer.add(imageLayer);

		Font font = GameFont.REGULAR.atSize(22f);
		final ViewData viewData = VIEW_DATA.get(event);
		IRectangle bounds = viewData.uiBounds;

		GroupLayer.Clipped uiLayer = graphics().createGroupLayer(
				bounds.width(), bounds.height());
		uiLayer.setTranslation(bounds.x(), bounds.y());
		layer.add(uiLayer);

		Root root = PlayingScreen.screen().iface.createRoot(
				AxisLayout.vertical(), BoneWarsStylesheetFactory.sheet(),
				uiLayer)//
				.setSize(uiLayer.width(), uiLayer.height());

		final Button closeButton = new Button("Okay!")//
				.addStyles(Style.FONT.is(font));

		root.add(new Label(event.storyText)//
				.addStyles(Style.TEXT_WRAP.on,//
						Style.FONT.is(font),//
						Style.VALIGN.center),//
				closeButton);

		viewData.music.play();

		root.add(closeButton);

		closeButton.onClick(new Slot<Button>() {
			@Override
			public void onEmit(Button button) {
				onClose.emit();
				viewData.music.sound.stop();
			}
		});
	}

	private Image lookupGameImageWithSameNameAs(StoryEvent event) {
		GameImage gameImage = GameImage.valueOf(event.toString());
		checkState(gameImage != null, "Missing image for " + event.toString());
		return gameImage.image;
	}

	public HasSize layer() {
		return layer;
	}

	private static final class ViewData {
		public IRectangle uiBounds;
		public GameMusic music;

		ViewData(IRectangle uiBounds, GameMusic music) {
			this.uiBounds = uiBounds;
			this.music = music;
		}
	}

}
