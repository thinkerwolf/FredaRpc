package com.thinkerwolf.freda.common.util;

import java.lang.reflect.Field;

public class ReflectionUtils {

    public static Class<?> getClassByName(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            //throw new RuntimeException("class " + name + " not exist");
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

    public static Object getPrimitiveFieldValue(Field field, String str) {
        Class<?> clazz = field.getType();
        if (clazz == String.class) {
            return str;
        } else if (clazz == byte.class || clazz == Byte.class) {
            return Byte.parseByte(str);
        } else if (clazz == short.class || clazz == Short.class) {
            return Short.parseShort(str);
        } else if (clazz == int.class || clazz == Integer.class) {
            return Integer.parseInt(str);
        } else if (clazz == long.class || clazz == Long.class) {
            return Long.parseLong(str);
        } else if (clazz == float.class || clazz == Float.class) {
            return Float.parseFloat(str);
        } else if (clazz == double.class || clazz == Double.class) {
            return Double.parseDouble(str);
        } else if (clazz == boolean.class || clazz == Boolean.class) {
            return Boolean.parseBoolean(str);
        }
        return null;
    }

}
