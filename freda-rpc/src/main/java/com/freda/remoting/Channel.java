package com.freda.remoting;

public interface Channel {

    void send(Object msg);

    void close();

}
