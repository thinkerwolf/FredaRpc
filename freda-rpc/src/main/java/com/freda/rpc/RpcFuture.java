package com.freda.rpc;

import java.util.Arrays;

/**
 * 参考netty promise
 */
public class RpcFuture {
	private static final Object SUCCESS = new Object();
	private static final Object FAIL = new Object();
	private static final Object UNCANCELLABLE = new Object();
	/**  */
	private Object result;
	private int waiters;
	private Object waitState;
	private RpcFutureListener[] listeners;
	private int size;
	
	public RpcFuture() {
		this.waitState = UNCANCELLABLE;
		this.waiters = 0;
		this.listeners = new RpcFutureListener[2];
		this.size = 2;
	}

	public Object getResult() {
		return result;
	}

	public void sync() throws InterruptedException {
		if (isDone()) {
			return;
		}
		if (Thread.interrupted()) {
			throw new InterruptedException("Thread " + Thread.currentThread().getName() + " is interrupted");
		}
		synchronized (this) {
			while (!isDone()) {
				try {
					waiters++;
					wait();
				} finally {
					waiters--;
				}
			}
		}
	}

	public boolean isDone() {
		return this.waitState != UNCANCELLABLE;
	}

	public void setSuccess(boolean success, Object result) {
		setSuccess0(success, result);
	}

	private void setSuccess0(boolean success, Object result) {
		this.result = result;
		this.waitState = success ? SUCCESS : FAIL;
		checkNotifyWaiters();
		notifyListeners();
	}

	public boolean isSuccess() {
		return this.waitState == SUCCESS;
	}

	private synchronized void checkNotifyWaiters() {
		if (waiters > 0) {
			notifyAll();
		}
	}

	public void addListener(RpcFutureListener l) {
		if (isDone()) {
			notifyListener(l);
			return;
		}
		RpcFutureListener[] listeners = this.listeners;
		final int size = this.size;
		if (size == listeners.length) {
			this.listeners = listeners = Arrays.copyOf(listeners, size << 1);
		}
		listeners[size] = l;
		this.size = size + 1;
	}

	private void notifyListener(final RpcFutureListener l) {
		if (isSuccess()) {
			l.onSuccess(result);
		} else {
			l.onFailure();
		}
	}

	private void notifyListeners() {
		for (RpcFutureListener l : listeners) {
			if (l != null) {
				notifyListener(l);
			}
		}
	}

}