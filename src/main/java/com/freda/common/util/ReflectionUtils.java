package com.freda.common.util;

public class ReflectionUtils {

	public static Class<?> getClassByName(String name) {
		try {
			return Class.forName(name);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Object newInstance(Class<?> clazz, Object... params) {
		try {
			if (params == null || params.length <= 0) {
				return clazz.newInstance();
			} else {
				Class<?>[] parmTypes = new Class<?>[params.length];
				for (int i = 0; i < params.length; i++) {
					parmTypes[i] = params.getClass();
				}
				return clazz.getConstructor(parmTypes).newInstance(params);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
