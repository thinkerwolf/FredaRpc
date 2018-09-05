package com.freda.remoting.netty;

import com.freda.remoting.RpcFuture;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

public class NettyChannel implements com.freda.remoting.Channel {

    private Channel channel;

    public NettyChannel(Channel channel) {
        this.channel = channel;
    }

    @Override
    public RpcFuture send(Object msg) {
        ChannelFuture cf =  channel.writeAndFlush(msg);
        final RpcFuture f = new RpcFuture();
        if (cf.isDone()) {
        	f.setSuccess(true);
        } else {
        	cf.addListener(new ChannelFutureListener() {
    			@Override
    			public void operationComplete(ChannelFuture future) throws Exception {
    				f.setSuccess(true);
    			}
    		});
        }
        return f;
    }

    @Override
    public void close() {
        channel.close();
    }


}
