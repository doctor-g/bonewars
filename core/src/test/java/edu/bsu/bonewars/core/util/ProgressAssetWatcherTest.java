package edu.bsu.bonewars.core.util;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import playn.core.Image;
import playn.core.util.Callback;

public class ProgressAssetWatcherTest {

	private ProgressAssetWatcher watcher;

	@Before
	public void setUp() {
		watcher = new ProgressAssetWatcher();
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testSize_zeroAssets() {
		assertEquals(0, watcher.total());
	}

	@Test
	public void testSize_oneAsset() {
		watcher.add(mock(Image.class));
		assertEquals(1, watcher.total());
	}

	@Test
	public void testLoaded_none() {
		assertEquals(0, numberLoaded());
	}

	private int numberLoaded() {
		return watcher.loaded().get().intValue();
	}

	public void testLoaded_one() {
		final Image image = mock(Image.class);
		configureSuccessfulLoadingOf(image);
		watcher.add(image);
		assertEquals(1, numberLoaded());
	}

	@Captor
	private ArgumentCaptor<Callback<Image>> imageCallbackCaptor;

	private void configureSuccessfulLoadingOf(final Image image) {
		doAnswer(new Answer<Callback<Image>>() {
			@Override
			public Callback<Image> answer(InvocationOnMock invocation)
					throws Throwable {
				imageCallbackCaptor.getValue().onSuccess(image);
				return null;
			}
		}).when(image).addCallback(imageCallbackCaptor.capture());
	}

}
