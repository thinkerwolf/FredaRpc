package com.freda.rpc;

import java.lang.reflect.InvocationTargetException;

public interface Exporter<T> {
	Class<T> getType();

	String id();

	T ref();

	Object invoke(String methodName, Class<?>[] parameterTypes, Object[] parameterValues) throws NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException;
}
