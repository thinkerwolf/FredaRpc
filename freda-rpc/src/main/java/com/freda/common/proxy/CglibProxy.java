package com.freda.common.proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * cglib代理
 *
 * @author wukai
 */
public class CglibProxy implements Proxy {

    private static final CglibProxy INSTANCE = new CglibProxy();

    public static CglibProxy getInstance() {
        return INSTANCE;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T newProxy(Class<T> clazz, final ProxyHandler handler) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                return handler.invoke(obj, method, args);
            }
        });
        return (T) enhancer.create();
    }

}
