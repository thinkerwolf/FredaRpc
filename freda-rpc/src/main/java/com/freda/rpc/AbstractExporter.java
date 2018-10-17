package com.freda.rpc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import com.freda.common.Net;

public abstract class AbstractExporter<T> implements Exporter<T> {

    protected String id;

    protected Class<T> type;

    protected T ref;

    protected volatile boolean destory;
    
    private ExporterInvokeListener[] listeners;
    
    private volatile int size;
    
    public AbstractExporter(String id, Class<T> type, T ref) {
        this.id = id;
        this.type = type;
        this.ref = ref;
        this.listeners = new ExporterInvokeListener[1];
        this.size = 1;
    }

    @Override
    public Class<T> getType() {
        return type;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public T ref() {
        return ref;
    }

    @Override
    public Object invoke(Net net, String methodName, Class<?>[] parameterTypes, Object[] parameterValues)
            throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException {
        Method method = ref.getClass().getMethod(methodName, parameterTypes);
        method.setAccessible(true);
        Object res = method.invoke(ref, parameterValues);
        notifyListeners(net, methodName, parameterTypes, parameterValues);
        return res;
    }
    
    @Override
    public synchronized void addListener(ExporterInvokeListener listener) {
    	ExporterInvokeListener[] listeners = this.listeners;
    	final int size = this.size;
    	if (size == listeners.length) {
    		this.listeners = listeners = Arrays.copyOf(listeners, size << 1);
    	}
    	listeners[size] = listener;
    	this.size = size + 1;
    }
    
    private void notifyListeners(Net net, String methodName, Class<?>[] parameterTypes, Object[] parameterValues) {
    	for (ExporterInvokeListener listener : listeners) {
    		if (listener != null) {
    			listener.invocation(net, methodName, parameterTypes, parameterValues);
    		}
    	}
    }
    
}
