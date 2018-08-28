package com.freda.rpc;

public interface Exporter<T> {
	Class<T> getType();

	String id();
	
	T ref();
}
