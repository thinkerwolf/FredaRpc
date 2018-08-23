package com.freda.remoting;

import com.freda.remoting.netty.NettyChannel;

public class Channels {
	
	public static Channel newNettyChannel(io.netty.channel.Channel ch) {
		NettyChannel nettyChannel = new NettyChannel(ch);
		return nettyChannel;
	}
	
	
}
