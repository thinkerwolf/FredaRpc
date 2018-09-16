package com.freda.remoting;

import com.freda.common.Net;

public interface Remoting {

    void stop();


    Net config();

    RemotingHandler handler();

    Channel channel();

    Channel start();

}
