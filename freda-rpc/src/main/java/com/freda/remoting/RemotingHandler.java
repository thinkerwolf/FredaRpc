package com.freda.remoting;

import com.freda.rpc.RpcFuture;

/**
 * 处理
 *
 * @author wukai
 */
public interface RemotingHandler {

	RpcFuture send(Remoting remoting, Object msg);

    void received(Remoting remoting, Object msg);

}
