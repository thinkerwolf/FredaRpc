package com.thinkerwolf.freda.common.concurrent;

public interface FutureListener<F extends Future<?>> {

    public void operationComplete(F future) throws Throwable;

}
