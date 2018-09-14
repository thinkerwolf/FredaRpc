package com.freda.common.concurrent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.freda.rpc.RpcFutureListener;

public abstract class AbstractFuture<V> implements Future<V> {

	private static final Object SUCCESS = new Object();
	private static final Object FAIL = new Object();
	private static final Object UNCANCELLABLE = new Object();

	private V result;
	private int waiters;
	private Object waitState;
	private FutureListener<? extends Future<V>>[] listeners;
	private int size;
	
	public AbstractFuture() {
		this.waiters = 0;
		this.listeners = new FutureListener[2];
		this.size = 2;
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
	public boolean isDone() {
		return this.waitState != UNCANCELLABLE;
	}

	@Override
	public V get() throws InterruptedException, ExecutionException {
		return result;
	}

	@Override
	public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		unit.sleep(timeout);
		return result;
	}

	@Override
	public Future<V> addListener(FutureListener<? extends Future<? super V>> listener) {
		
		
		
		return this;
	}

	@Override
	public Future<V> addListeners(FutureListener<? extends Future<? super V>>... listeners) {
		return this;
	}

	@Override
	public Future<V> sync() throws InterruptedException {
		if (isDone()) {
			return this;
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
		return this;
	}
	
	private void notifyListener(final FutureListener<? extends Future<? super V>> l) {
		if (isDone()) {
			//l.operationComplete(this);
		}
	}

	private void notifyListeners() {
		for (FutureListener<? extends Future<V>> l : listeners) {
			if (l != null) {
				notifyListener(l);
			}
		}
	}
	
}
