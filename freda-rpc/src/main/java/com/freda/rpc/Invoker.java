package com.freda.rpc;

import com.freda.remoting.RequestMessage;

public interface Invoker<T> {
	/**
	 * invoke sync
	 * @param inv
	 * @return
	 * @throws RpcException
	 */
	Result invoke(RequestMessage inv) throws RpcException;
	/**
	 * rpc interface
	 * @return
	 */
	Class<T> getType();
	/**
	 * id
	 * @return
	 */
	String id();
}
