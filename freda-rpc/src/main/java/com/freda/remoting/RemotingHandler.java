package com.freda.remoting;

import com.freda.common.concurrent.Future;

/**
 * 处理
 *
 * @author wukai
 */
public interface RemotingHandler {

    Future<?> send(Remoting remoting, Object msg);

    void received(Remoting remoting, Object msg);
    
    Class<?> decodeClass();
     
}
