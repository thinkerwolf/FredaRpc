package com.thinkerwolf.freda.remoting;

import com.thinkerwolf.freda.common.Net;

public interface Channel {

    void send(Object msg);

    void close();
    
    Net net();
}
