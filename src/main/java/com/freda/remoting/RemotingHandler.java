package com.freda.remoting;

/**
 * 处理
 *
 * @author wukai
 */
public interface RemotingHandler {

	RpcFuture send(Remoting remoting, Object msg);

    void received(Remoting remoting, Object msg);

}
