package com.freda.remoting;

import com.freda.common.concurrent.Future;

/**
 * 处理
 *
 * @author wukai
 */
public interface RemotingHandler {

    Future<?> send(Channel channel, Object msg);

    void received(Channel channel, Object msg);
    
    Class<?> decodeClass();
     
}
