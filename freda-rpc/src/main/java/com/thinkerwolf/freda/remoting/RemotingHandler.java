package com.thinkerwolf.freda.remoting;

import com.thinkerwolf.freda.common.concurrent.Future;

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
