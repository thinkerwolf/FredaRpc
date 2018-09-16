package com.freda.common.concurrent;

import java.util.Arrays;

public abstract class AbstractFuture<V> implements Future<V> {

	// private static final Object SUCCESS = new Object();
	// private static final Object FAIL = new Object();
	// private static final Object UNCANCELLABLE = new Object();

	// private Object waitState;

	@SuppressWarnings("rawtypes")
	private FutureListener[] listeners;
	private int size;

	public AbstractFuture() {
		this.listeners = new FutureListener[1];
		this.size = 1;
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return false;
	}

	@Override
	public boolean isCancelled() {
		return false;
	}

	@Override
	public Future<V> addListener(FutureListener listener) {
		FutureListener[] listeners = this.listeners;
		final int size = this.size;
		if (size == listeners.length) {
			this.listeners = listeners = Arrays.copyOf(listeners, size << 1);
		}
		listeners[size] = listener;
		this.size = size + 1;
		if (isDone()) {
			notifyListener(listener);
		}
		return this;
	}

	@Override
	public Future<V> addListeners(FutureListener ... ls) {
		if (ls.length == 0 || ls == null) {
			return this;
		}
		for (FutureListener l : ls) {
			addListener(l);
		}
		return this;
	}

	protected void notifyListener(final FutureListener l) {
		if (isDone()) {
			try {
				l.operationComplete(this);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	protected void notifyListeners() {
		for (FutureListener<Future<V>> l : listeners) {
			if (l != null) {
				notifyListener(l);
			}
		}
	}

}
