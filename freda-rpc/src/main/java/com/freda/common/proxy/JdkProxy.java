package com.freda.common.proxy;

import java.lang.reflect.Method;

/**
 * jdk代理
 *
 * @author wukai
 */
public class JdkProxy implements Proxy {

    private static final JdkProxy INSTANCE = new JdkProxy();

    public static JdkProxy getInstance() {
        return INSTANCE;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T newProxy(Class<T> clazz, final ProxyHandler handler) {

        return (T) java.lang.reflect.Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz},
                new java.lang.reflect.InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        return handler.invoke(proxy, method, args);
                    }
                });
    }

}
