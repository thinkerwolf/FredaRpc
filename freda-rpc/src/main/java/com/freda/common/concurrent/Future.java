package com.freda.common.concurrent;

public interface Future<V> extends java.util.concurrent.Future<V> {

    boolean isSuccess();

    Future<V> addListener(FutureListener<? extends Future<? super V>> listener);

    @SuppressWarnings("unchecked")
    Future<V> addListeners(FutureListener<? extends Future<? super V>>... listeners);

    Future<V> sync() throws InterruptedException;

    Throwable cause();

}
