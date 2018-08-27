package com.freda.rpc;

import com.freda.remoting.RequestMessage;

public interface Invoker<T> {

	Object invoke(RequestMessage inv) throws RpcException;
	
	Class<T> getType();
	
}
