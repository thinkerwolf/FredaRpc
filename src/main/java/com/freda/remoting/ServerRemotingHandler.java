package com.freda.remoting;

import com.freda.config.ServiceConfig;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ServerRemotingHandler implements RemotingHandler {
    protected ConcurrentMap<String, ServiceConfig<?>> serviceMap = new ConcurrentHashMap<>();

    @Override
    public Object send(Remoting remoting, Object msg) {
        remoting.channel().send(msg);
        return null;
    }

    @Override
    public void received(Remoting remoting, Object msg) {
        RequestMessage requestMessage = (RequestMessage) msg;
        ResponseMessage responseMessage = new ResponseMessage();
        try {
            Object obj = null;
            ServiceConfig<?> serviceConfig = getServiceConfig(requestMessage.getClazzName());
            responseMessage.setId(requestMessage.getId());
            if (serviceConfig != null) {
                obj = serviceConfig.getRef();
                Method method = serviceConfig.getInterfaceClass().getMethod(requestMessage.getMethodName(),
                        requestMessage.getParameterTypes());
                method.setAccessible(true);
                Object result = method.invoke(obj, requestMessage.getArgs());
                responseMessage.setSuccess(true);
                responseMessage.setResult(result);
            } else {
                responseMessage.setSuccess(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        responseMessage.setSuccess(false);
        send(remoting, responseMessage);
    }

    public void addServiceConfig(ServiceConfig<?> sc) {
        serviceMap.put(sc.getId(), sc);
    }

    public void removeServiceConfig(ServiceConfig<?> sc) {
        serviceMap.remove(sc.getId());
    }

    public ServiceConfig<?> getServiceConfig(Class<?> clazz) {
        for (ServiceConfig<?> sc : serviceMap.values()) {
            if (sc.getInterfaceClass().equals(clazz)) {
                return sc;
            }
        }
        return null;
    }

    public ServiceConfig<?> getServiceConfig(String id) {
        return serviceMap.get(id);
    }

}
