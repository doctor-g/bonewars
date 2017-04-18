package edu.bsu.bonewars.core.view;

import java.util.List;

import playn.core.Keyboard;
import playn.core.Keyboard.Event;

import com.google.common.collect.ImmutableList;

import edu.bsu.bonewars.core.PlayingScreen;
import edu.bsu.bonewars.core.event.PublicationEvent;
import edu.bsu.bonewars.core.model.Fame;
import edu.bsu.bonewars.core.model.Fossil;
import edu.bsu.bonewars.core.model.Fossil.Quality;
import edu.bsu.bonewars.core.model.Fossil.Type;
import edu.bsu.bonewars.core.model.Funds;
import edu.bsu.bonewars.core.model.Game;
import edu.bsu.bonewars.core.model.Player;
import edu.bsu.bonewars.core.model.Site;
import edu.bsu.bonewars.core.model.StoryEvent;
import edu.bsu.bonewars.core.util.IgnoreSuccessCallback;

public class CheatCodeListener extends Keyboard.Adapter {

	private final Game game = Game.currentGame();

	@Override
	public void onKeyDown(Event event) {
		switch (event.key()) {
		case D:
			setupAmbiguousPublishAndShowDialog();
			break;
		case A:
			Game.currentGame().addAvailableSite();
			break;
		case M:
			Game.currentGame().endGameMarshWins();
			break;
		case C:
			Game.currentGame().endGameCopeWins();
			break;
		case S:
			Game.currentGame().doStoryEvent(StoryEvent.MARSH_CHASED_BY_SIOUX,
					new IgnoreSuccessCallback());
			break;
		case L:
			Game.currentGame().doStoryEvent(StoryEvent.COPE_FAILS_SILVER_MINE,
					new IgnoreSuccessCallback());
			break;
		case P:
			Game.currentGame().doStoryEvent(
					StoryEvent.FOSSIL_INFLUX_ADD_FUNDS_TO_PLAYERS,
					new IgnoreSuccessCallback());
			break;
		case H:
			PlayingScreen.screen().showUpkeepDialog();
			break;
		case K1:
			setMarshFunds(1);
			break;
		case K3:
			setMarshFunds(3);
			break;
		case K0:
			setMarshFunds(0);
			break;
		case T:
			game.doFossilHunterAuction();
			break;
		case R:
			Game.currentGame().endRound();
			break;
		case B:
			giveMarshAmbiguouslyPublishableFossils();
			break;
		case X:
			for (Site aSite : Game.currentGame().sites()) {
				while (aSite.hasNextFossil()) {
					aSite.excavateNextFossil();
				}
			}
			break;
		case W:
			Game.currentGame().currentPlayer().get().addLittleWorker();
			break;
		case Z:
			giveMarshAnalyzedFossil();
			break;
		case F:
			fillPublishArea();
			break;
		case U:
			setUpTrump(game.marsh);
			break;
		case Y:
			setUpTrump(game.cope);
			break;
		case O:
			setUpBouncedPublishAttempt();
			break;
		case EQUALS:
			GameMusic.SECONDTHEME.play();
			break;
		default:
			// Do nothing.
		}
	}

	private List<Fossil> giveMarshAmbiguouslyPublishableFossils() {
		List<Fossil> fossils = ImmutableList.of(
				Fossil.createWithQuality(Quality.MEDIUM).andWithType(Type.A),
				Fossil.createWithQuality(Quality.LOW).andWithType(Type.A)
						.analyze());
		for (Fossil f : fossils)
			game.marsh.addFossil(f);
		return fossils;
	}

	private void setupAmbiguousPublishAndShowDialog() {
		List<Fossil> fossils = giveMarshAmbiguouslyPublishableFossils();

		AmbiguousPublishDialog dialog = AmbiguousPublishDialog.create(
				game.actionRegistry().getAnalyzedPublishActionForFossilStack(
						game.marsh.fossilStack(fossils.get(0).type())),
				game.actionRegistry().getUnanalyzedPublishActionForFossilStack(
						game.marsh.fossilStack(fossils.get(1).type())));
		dialog.show();
	}

	private void setMarshFunds(int i) {
		Game.currentGame().marsh.setFunds(Funds.valueOf(i));
	}

	private void giveMarshAnalyzedFossil() {
		game.marsh.addFossil(Fossil.createWithQuality(Quality.MEDIUM)
				.andWithType(Type.A).analyze());
	}

	private void fillPublishArea() {
		boolean marsh = true;
		for (Fossil.Type type : Fossil.Type.values()) {
			Fossil fossil = Fossil.createWithQuality(Quality.VERY_LOW)
					.andWithType(type);
			fossil.setOwner(marsh ? game.marsh : game.cope);
			marsh = !marsh;
			showPublication(fossil);
		}
	}

	private void showPublication(Fossil fossil) {
		game.eventBus.add(PublicationEvent.create(game.marsh, fossil, //
				Fame.valueOf(fossil.quality().ordinal() + 1)));
	}

	private void setUpTrump(Player trumper) {
		trumper.opponent().addFame(Fame.valueOf(5));

		Fossil fossil = Fossil.createWithQuality(Quality.VERY_LOW).andWithType(
				Type.A);
		fossil.setOwner(trumper.opponent());
		game.publishedFossilsArea().publish(fossil);
		showPublication(fossil);

		Fossil trump = Fossil.createWithQuality(Quality.MEDIUM)
				.andWithType(Type.A)//
				.analyze();
		trumper.addFossil(trump);
	}

	private void setUpBouncedPublishAttempt() {
		game.marsh.addFame(Fame.valueOf(5));
		Fossil fossil = Fossil.createWithQuality(Quality.HIGH).andWithType(
				Type.A);
		fossil.setOwner(game.cope);
		game.publishedFossilsArea().publish(fossil);
		showPublication(fossil);

		Fossil trump = Fossil.createWithQuality(Quality.MEDIUM)//
				.andWithType(Type.A);
		game.marsh.addFossil(trump);
	}

}