package com.thinkerwolf.freda.rpc;

import java.lang.reflect.InvocationTargetException;

import com.thinkerwolf.freda.common.Net;

public interface Exporter<T> {
    Class<T> getType();

    String id();

    T ref();

    Object invoke(Net net, String methodName, Class<?>[] parameterTypes, Object[] parameterValues) throws NoSuchMethodException,
            SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException;


    void destory();
    
    void addListener(ExporterInvokeListener listener);
}
