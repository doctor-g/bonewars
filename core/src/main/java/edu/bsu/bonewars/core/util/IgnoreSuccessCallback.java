package edu.bsu.bonewars.core.util;

import playn.core.util.Callback;

public final class IgnoreSuccessCallback implements Callback<Void> {

	@Override
	public void onSuccess(Void result) {
	}

	@Override
	public void onFailure(Throwable cause) {
		throw new RuntimeException(cause);
	}

}
