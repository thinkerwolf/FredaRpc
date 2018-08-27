package com.freda.remoting.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

public class NettyChannel implements com.freda.remoting.Channel {

    private Channel channel;

    public NettyChannel(Channel channel) {
        this.channel = channel;
    }

    @Override
    public void send(Object msg) {
        ChannelFuture cf = channel.writeAndFlush(msg);
        try {
            cf.sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        channel.close();
    }


}
