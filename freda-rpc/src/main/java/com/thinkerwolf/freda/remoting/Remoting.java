package com.thinkerwolf.freda.remoting;

import com.thinkerwolf.freda.common.Net;

public interface Remoting {

    void stop();

    Net config();

    RemotingHandler handler();

    Channel channel();

    Channel start();

}
