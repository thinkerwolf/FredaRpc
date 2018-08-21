package com.freda.config.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import com.freda.common.annotation.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * @author wukai
 */
public class RpcBeanPostProcessor implements BeanPostProcessor {

    private ConcurrentMap<String, Object> serviceMap = new ConcurrentHashMap<String, Object>();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> clazz = bean.getClass();
        Service service = clazz.getAnnotation(Service.class);
        if (service != null) {
            String serviceName = service.value();
            serviceMap.putIfAbsent(serviceName, bean);
        }
        return bean;
    }

    public Object refer(String serviceName) {
        return serviceMap.get(serviceName);
    }

    public void destory() {
        serviceMap.clear();
        serviceMap = null;
    }

}
