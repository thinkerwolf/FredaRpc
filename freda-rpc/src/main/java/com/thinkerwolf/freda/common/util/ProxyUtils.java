package com.thinkerwolf.freda.common.util;

import com.thinkerwolf.freda.common.proxy.CglibProxy;
import com.thinkerwolf.freda.common.proxy.JdkProxy;
import com.thinkerwolf.freda.common.proxy.ProxyHandler;

public class ProxyUtils {

    public static <T> T newProxy(Class<T> clazz, ProxyHandler handler) {
        if (clazz.isInterface()) {
            return JdkProxy.getInstance().newProxy(clazz, handler);
        } else {
            return CglibProxy.getInstance().newProxy(clazz, handler);
        }
    }
}
