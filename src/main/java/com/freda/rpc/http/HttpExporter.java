package com.freda.rpc.http;


import com.freda.rpc.AbstractExporter;

public class HttpExporter<T> extends AbstractExporter<T> {

	public HttpExporter(String id, Class<T> type, T ref) {
		super(id, type, ref);
	}

}
