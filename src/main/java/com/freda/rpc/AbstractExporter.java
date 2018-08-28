package com.freda.rpc;

public abstract class AbstractExporter<T> implements Exporter<T> {
	protected String id;

	protected Class<T> type;
	
	protected  T ref;
	
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
}
