package com.thinkerwolf.freda.common.proxy;

/**
 * 代理
 *
 * @author wukai
 */
public interface Proxy {

    <T> T newProxy(Class<T> clazz, ProxyHandler handler);

}
