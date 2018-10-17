package com.freda.remoting;

import com.freda.common.Net;

public interface Channel {

    void send(Object msg);

    void close();
    
    Net net();
}
