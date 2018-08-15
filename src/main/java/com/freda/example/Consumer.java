package com.freda.example;

import com.freda.remoting.netty.NettyClient;

public class Consumer {
    public static void main(String[] args) throws Exception {
        final NettyClient client = new NettyClient();

        client.doInit();
        // 模拟并发的情况

        try {
            // 在此基础上进行封装
            client.doConnect("10.8.10.69", 8080);
            final DemoService demoService = client.refer("rpc@demoService", DemoService.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.in.read();
        client.doStop();
    }
}
