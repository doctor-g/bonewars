package edu.bsu.bonewars.core.view;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import playn.core.Assets;
import playn.core.Image;
import playn.core.Platform;
import playn.core.PlayN;
import playn.core.StubPlatform;
import edu.bsu.bonewars.core.model.StoryEvent;

public class StoryIntegrationTest {

	@Before
	public void setUp() {
		Platform testPlaf = new StubPlatform() {
			@Override
			public Assets assets() {
				Assets assets = mock(Assets.class);
				Image mockImage = mock(Image.class);
				when(assets.getImage(Matchers.anyString()))//
						.thenReturn(mockImage);
				return assets;
			}
		};
		PlayN.setPlatform(testPlaf);
	}

	@Test
	public void testAllStoryEventsHaveCorrespondingGameImages() {
		for (StoryEvent event : StoryEvent.values()) {
			GameImage gameImage = GameImage.valueOf(event.toString());
			assertNotNull("Missing image for " + event.toString(), gameImage);
		}
	}
}
