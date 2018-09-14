package com.freda.rpc;


public interface AsyncCallback<T> {
	
	void onCompletion(T result);

	void onError(Throwable t);

}
