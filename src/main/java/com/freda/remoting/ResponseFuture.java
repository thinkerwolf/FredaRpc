package com.freda.remoting;

import io.netty.util.Signal;
import io.netty.util.concurrent.DefaultPromise;

/**
 * 参考netty {@link DefaultPromise}
 */
public class ResponseFuture {
    private static final Signal SUCCESS = Signal.valueOf(ResponseFuture.class, "SUCCESS");
    private static final Signal UNCANCELLABLE = Signal.valueOf(ResponseFuture.class, "UNCANCELLABLE");
    /**  */
    private Object result;
    private int waiters;
    private Object waitState;

    public ResponseFuture() {
        waitState = UNCANCELLABLE;
        this.waiters = 0;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
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
        return this.waitState == SUCCESS;
    }

    public void setSuccess(Object result) {
        setSuccess0(result);
    }

    private void setSuccess0(Object result) {
        this.waitState = result == null ? SUCCESS : result;
        checkNotifyWaiters();
    }

    private synchronized void checkNotifyWaiters() {
        if (waiters > 0) {
            notifyAll();
        }
    }
}