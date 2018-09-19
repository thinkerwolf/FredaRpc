package com.freda.common.concurrent;


import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class DefaultPromise<V> extends AbstractFuture<V> implements Promise<V> {


    private static final Object SUCCESS = new Object();
    private static final Object FAIL = new Object();
    private static final Object UNCANCELLABLE = new Object();


    private volatile Object result;
    private Object waitState;
    private int waiters;
    private Throwable cause;

    public DefaultPromise() {
        this.waitState = UNCANCELLABLE;
    }


    @Override
    public boolean isSuccess() {
        return waitState == SUCCESS;
    }

    @Override
    public void setSuccess(V result) {
        setSuccess0(result);
    }

    @Override
    public Throwable cause() {
        return cause;
    }

    @Override
    public boolean isDone() {
        return this.waitState != UNCANCELLABLE;
    }

    @SuppressWarnings("unchecked")
	@Override
    public V get() throws InterruptedException, ExecutionException {
        if (result == SUCCESS || result == FAIL) {
            return null;
        }
        return (V) result;
    }

    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        unit.sleep(timeout);
        return get();
    }

    private void setSuccess0(V result) {
        this.result = result == null ? SUCCESS : result;
        this.waitState = SUCCESS;
        checkNotifyWaiters();
        notifyListeners();
    }

    @Override
    public void setFailure(Throwable cause) {
        setFailure0(cause);
    }


    private void setFailure0(Throwable cause) {
        this.cause = cause;
        this.result = FAIL;
        this.waitState = FAIL;
    }

    private synchronized void checkNotifyWaiters() {
        if (waiters > 0) {
            notifyAll();
        }
    }

    @Override
    public Future<V> sync() throws InterruptedException {
        if (isDone()) {
            notifyListeners();
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
}
