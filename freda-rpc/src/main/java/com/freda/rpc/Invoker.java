package com.freda.rpc;

public interface Invoker<T> {
    /**
     * invoke sync
     *
     * @param inv
     * @return
     * @throws RpcException
     */
    Result invoke(RequestMessage inv) throws RpcException;

    /**
     * invoke async if callback is not null, otherwise sync
     *
     * @param inv
     * @param callback
     * @return
     * @throws RpcException
     */
    Result invoke(RequestMessage inv, boolean isAsync) throws RpcException;

    /**
     * rpc interface
     *
     * @return
     */
    Class<T> getType();

    /**
     * id
     *
     * @return
     */
    String id();

    void destory();

}
