package com.freda.remoting;

public interface Channel {

    RpcFuture send(Object msg);

    void close();

}
