package com.freda.common.proxy;

import java.lang.reflect.Method;

public interface ProxyHandler {

    Object invoke(Object proxy, Method method, Object[] args) throws Throwable;

}