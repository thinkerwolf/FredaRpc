package com.freda.remoting.netty;


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

public class NettyChannel implements com.freda.remoting.Channel {

    private Channel channel;
    
    public NettyChannel(Channel channel) {
        this.channel = channel;
    }
    
    private static final ConcurrentMap<Channel, NettyChannel> channelMap = new ConcurrentHashMap<>();
    
    static NettyChannel getOrAddChannel(Channel channel) {
    	NettyChannel nettyChannel = channelMap.get(channel);
    	if (nettyChannel == null) {
    		nettyChannel = new NettyChannel(channel);
    		nettyChannel = channelMap.putIfAbsent(channel, nettyChannel);
    	}
    	return nettyChannel;
    }
    
    @Override
    public void send(Object msg) {
        ChannelFuture cf =  channel.writeAndFlush(msg);
        boolean success = true;
        int timeout = 1000;
        try {
			success = cf.await(timeout);
		} catch (Throwable e) {
			 throw new RuntimeException("runtime", e);
		}
        if (!success) {
        	throw new RuntimeException("runtime");
        }
    }

    @Override
    public void close() {
        channel.close();
    }


}
