package edu.bsu.bonewars.core.util;

import playn.core.util.Callback;

public abstract class ExceptionThrowingOnFailureCallback<T> implements
		Callback<T> {

	@Override
	public final void onFailure(Throwable cause) {
		throw new RuntimeException(cause);
	}

}
