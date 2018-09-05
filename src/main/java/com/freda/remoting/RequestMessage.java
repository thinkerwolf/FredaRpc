package com.freda.remoting;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RequestMessage implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -5906021628390669682L;
	String clazzName;
	String methodName;
	Object[] args;
	Class<?>[] parameterTypes;
	private int id;
	private Map<String, Object> parameters;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public Object[] getArgs() {
		return args == null ? new Object[] {} : args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}

	public Class<?>[] getParameterTypes() {
		return parameterTypes == null ? new Class<?>[] {} : parameterTypes;
	}

	public void setParameterTypes(Class<?>[] parameterTypes) {
		this.parameterTypes = parameterTypes;
	}

	public String getClazzName() {
		return clazzName;
	}

	public void setClazzName(String clazzName) {
		this.clazzName = clazzName;
	}

	public void putParameter(String key, Object value) {
		if (parameters == null) {
			parameters = new HashMap<>();
		}
		parameters.put(key, value);
	}

	@SuppressWarnings("unchecked")
	public <T> T getParameter(String key, T defaultValue) {
		Object value = parameters == null ? null : parameters.get(key);
		return value == null ? defaultValue : (T) value;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("clazzName#" + clazzName + " ,args#" + Arrays.toString(args));
		sb.append(" ,method#" + methodName);
		return sb.toString();
	}

}
