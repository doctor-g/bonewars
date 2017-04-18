package edu.bsu.bonewars.core.view;

import static com.google.common.base.Preconditions.checkState;
import static playn.core.PlayN.assets;

import java.util.Map;

import playn.core.Image;

import com.google.common.collect.ImmutableMap;

import edu.bsu.bonewars.core.model.Fossil;
import edu.bsu.bonewars.core.model.Fossil.Quality;
import edu.bsu.bonewars.core.model.Fossil.Type;

public enum GameImage {

	LOADING_BACKGROUND("images/load_screen.png"), //
	BACKGROUND("images/bg.png"), //
	MARSH_WIN("images/End_Screen/marsh_win.png"), //
	COPE_WIN("images/End_Screen/cope_win.png"), //

	MARSH_WORKER("images/Workers/marsh_worker_icon.png"), //
	COPE_WORKER("images/Workers/cope_worker_icon.png"), //
	MARSH_WORKER_SELECTED("images/Workers/marsh_worker_selected.png"), //
	COPE_WORKER_SELECTED("images/Workers/cope_worker_selected.png"), //
	MARSH_WORKER_WORKED("images/Workers/used_marsh_worker_icon.png"), //
	COPE_WORKER_WORKED("images/Workers/used_cope_worker_icon.png"), //
	WORKER("images/Workers/worker_icon.png"), //
	WORKER_SELECTED("images/Workers/worker_selected.png"), //
	WORKER_WORKED("images/Workers/used_worker_icon.png"), //
	ROUND_PLACARD("images/round_placard.png"), //
	FAME_STAR("images/fame_star.png"), //
	BLACK_STAR("images/black_star.png"), //

	SITE_OWNED_BY_MARSH("images/Site/marsh_site.png"), //
	SITE_OWNED_BY_COPE("images/Site/cope_site.png"), //
	SITE_UNCLAIMED("images/Site/unclaimed_site.png"), // `

	ACQUIRE_FLAG("images/Actions/acquire_site_flag.png"), //
	ANALYZE_FLAG("images/Actions/analyze_flag.png"), //
	EXCAVATION_ICON("images/Actions/excavate_flag.png"), //
	RAISE_fUNDS("images/Actions/raise_funds_flag.png"), //
	PUBLISH_FLAG("images/Actions/publish_flag.png"), //
	HIRE_WORKER_FLAG("images/Actions/hire_worker_flag.png"), //

	FOSSIL_A("images/Fossil_Types/allosaurus_silhouette.png"), //
	FOSSIL_B("images/Fossil_Types/apatosaurus_silhouette.png"), //
	FOSSIL_C("images/Fossil_Types/Ceratosaurus_silhouette.png"), //
	FOSSIL_D("images/Fossil_Types/hadrosaurus_fossil_icon.png"), //
	FOSSIL_E("images/Fossil_Types/horse_fossil_icon.png"), //
	FOSSIL_F("images/Fossil_Types/pterodactyl_silhouette.png"), //
	FOSSIL_G("images/Fossil_Types/Stego_silhouette.png"), //
	FOSSIL_H("images/Fossil_Types/triceratops_fossil_icon.png"), //

	FOSSIL_WIDGET_BACKGROUND("images/Fossil_Types/Fossil_Widget_Box.png"), //
	PUBLISH_FOSSIL_WIDGET_BACKGROUND(
			"images/Fossil_Types/Publish_Fossil_Widget_Box.png"), //

	QUESTIONMARK_ZERO("images/QuestionMarks/question_mark_0.png"), //
	QUESTIONMARK_ONE("images/QuestionMarks/question_mark_1.png"), //
	QUESTIONMARK_TWO("images/QuestionMarks/question_mark_2.png"), //
	QUESTIONMARK_THREE("images/QuestionMarks/question_mark_3.png"), //
	QUESTIONMARK_FOUR("images/QuestionMarks/question_mark_4.png"), //
	QUESTIONMARK_FIVE("images/QuestionMarks/question_mark_5.png"), //

	QUALITY_VERY_LOW("images/Fossil_Completeness/quality_very_low.png"), // ;
	QUALITY_LOW("images/Fossil_Completeness/quality_low.png"), //
	QUALITY_MEDIUM("images/Fossil_Completeness/quality_medium.png"), //
	QUALITY_HIGH("images/Fossil_Completeness/quality_high.png"), //

	MARSH_CHASED_BY_SIOUX("images/StoryEvents/marsh_chased_wbox.png"), //
	COPE_FAILS_SILVER_MINE("images/StoryEvents/cope_failed_wbox.png"), //
	FOSSIL_INFLUX_ADD_FUNDS_TO_PLAYERS(
			"images/StoryEvents/extra_money_wbox.png"), //

	DIALOG_BG("images/Publish_Popout.png"), //

	FOSSIL_HUNTER_COPE("images/fossil_hunter_cope.png"), //
	FOSSIL_HUNTER_MARSH("images/fossil_hunter_marsh.png"), //

	OPTIONS_BACKGROUND("images/options_screen.png");

	public final Image image;

	private GameImage(String path) {
		this.image = assets().getImage(path);
	}

	private static final Map<Type, Image> fossilImages = ImmutableMap
			.<Type, Image> builder()//
			.put(Type.A, GameImage.FOSSIL_A.image)//
			.put(Type.B, GameImage.FOSSIL_B.image)//
			.put(Type.C, GameImage.FOSSIL_C.image)//
			.put(Type.D, GameImage.FOSSIL_D.image)//
			.put(Type.E, GameImage.FOSSIL_E.image)//
			.put(Type.F, GameImage.FOSSIL_F.image)//
			.put(Type.G, GameImage.FOSSIL_G.image)//
			.put(Type.H, GameImage.FOSSIL_H.image)//
			.build();

	public static Image getGameImageForFossilType(Fossil.Type type) {
		return fossilImages.get(type);
	}

	private static final Map<Integer, Image> questionMarkImages = ImmutableMap
			.<Integer, Image> builder()//
			.put(0, GameImage.QUESTIONMARK_ZERO.image)//
			.put(1, GameImage.QUESTIONMARK_ONE.image)//
			.put(2, GameImage.QUESTIONMARK_TWO.image)//
			.put(3, GameImage.QUESTIONMARK_THREE.image)//
			.put(4, GameImage.QUESTIONMARK_FOUR.image)//
			.put(5, GameImage.QUESTIONMARK_FIVE.image)//
			.build();

	public static Image questionMarksFor(Integer size) {
		checkState(size >= 0);
		if (size > 5) {
			return questionMarkImages.get(5);
		}
		return questionMarkImages.get(size);
	}

	private static final Map<Quality, Image> completenessImages = ImmutableMap
			.<Quality, Image> builder()//
			.put(Quality.VERY_LOW, GameImage.QUALITY_VERY_LOW.image)//
			.put(Quality.LOW, GameImage.QUALITY_LOW.image)//
			.put(Quality.MEDIUM, GameImage.QUALITY_MEDIUM.image)//
			.put(Quality.HIGH, GameImage.QUALITY_HIGH.image)//
			.build();

	public static Image completenessFor(Quality quality) {
		return completenessImages.get(quality);
	}
}
