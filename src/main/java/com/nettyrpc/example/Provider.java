package com.nettyrpc.example;

import com.nettyrpc.remote.NettyServer;

public class Provider {
    public static void main(String[] args) throws Exception {
        NettyServer server1 = new NettyServer();
        server1.doStart(8080);
        //server1.doStart(8081);
        //server1.doStart(8082);

        System.in.read();
    }
}
