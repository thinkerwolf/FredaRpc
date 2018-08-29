package com.freda.rpc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class AbstractExporter<T> implements Exporter<T> {

	protected String id;

	protected Class<T> type;

	protected T ref;

	public AbstractExporter(String id, Class<T> type, T ref) {
		this.id = id;
		this.type = type;
		this.ref = ref;
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
	public Object invoke(String methodName, Class<?>[] parameterTypes, Object[] parameterValues)
			throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		Method method = ref.getClass().getMethod(methodName, parameterTypes);
		method.setAccessible(true);
		return method.invoke(ref, parameterValues);
	}
}
