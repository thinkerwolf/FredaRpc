package com.freda.rpc;

public abstract class AbstractInvoker<T> implements Invoker<T> {
	private Class<T> type;
	
	public AbstractInvoker(Class<T> type) {
		this.type = type;
	}

	public Class<T> getType() {
		return type;
	}
	
}
