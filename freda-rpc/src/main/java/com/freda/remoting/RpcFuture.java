package com.freda.remoting;

import io.netty.util.Signal;
import io.netty.util.concurrent.DefaultPromise;

/**
 * 参考netty {@link DefaultPromise}
 */
public class RpcFuture {
    private static final Signal SUCCESS = Signal.valueOf(RpcFuture.class, "SUCCESS");
    private static final Signal FAIL = Signal.valueOf(RpcFuture.class, "FAIL");
    private static final Signal UNCANCELLABLE = Signal.valueOf(RpcFuture.class, "UNCANCELLABLE");
    /**  */
    private Object result;
    private int waiters;
    private Object waitState;
    
    public RpcFuture() {
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
        return this.waitState != UNCANCELLABLE;
    }

    public void setSuccess(boolean result) {
        setSuccess0(result);
    }

    private void setSuccess0(boolean result) {
        this.waitState = result ? SUCCESS : FAIL;
        checkNotifyWaiters();
    }
    
    public boolean isSuccess() {
    	return this.waitState == SUCCESS;
    }
    
    private synchronized void checkNotifyWaiters() {
        if (waiters > 0) {
            notifyAll();
        }
    }
}