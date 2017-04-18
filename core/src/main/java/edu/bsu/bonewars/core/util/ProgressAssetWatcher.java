package edu.bsu.bonewars.core.util;

import playn.core.AssetWatcher;
import playn.core.Image;
import playn.core.Sound;
import playn.core.util.Callback;
import react.Value;
import react.ValueView;

public class ProgressAssetWatcher extends AssetWatcher {

	private int total = 0;
	private final Value<Integer> loaded = Value.create(0);

	public ProgressAssetWatcher() {
		super();
	}

	public ProgressAssetWatcher(Listener listener) {
		super(listener);
	}

	@Override
	public void add(Image image) {
		total++;
		image.addCallback(new FailureIgnoringCallback<Image>() {
			@Override
			public void onSuccess(Image result) {
				incrementLoaded();
			}
		});
		super.add(image);
	}

	private void incrementLoaded() {
		loaded.update(loaded.get().intValue() + 1);
	}

	@Override
	public void add(Sound sound) {
		total++;
		sound.addCallback(new FailureIgnoringCallback<Sound>() {
			@Override
			public void onSuccess(Sound result) {
				incrementLoaded();
			}
		});
		super.add(sound);
	}

	public int total() {
		return total;
	}

	public ValueView<Integer> loaded() {
		return loaded;
	}

	private abstract class FailureIgnoringCallback<T> implements Callback<T> {

		@Override
		public void onFailure(Throwable cause) {
			// do nothing, someone else will handle it.
		}

	}
}
