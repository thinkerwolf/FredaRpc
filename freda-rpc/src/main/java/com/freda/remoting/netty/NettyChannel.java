package com.freda.remoting.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class NettyChannel implements com.freda.remoting.Channel {

	private static final ConcurrentMap<Channel, NettyChannel> channelMap = new ConcurrentHashMap<>();
	private Channel channel;

	public NettyChannel(Channel channel) {
		this.channel = channel;
	}

	static NettyChannel getOrAddChannel(Channel channel) {
		NettyChannel nettyChannel = channelMap.get(channel);
		if (nettyChannel == null) {
			nettyChannel = new NettyChannel(channel);
			channelMap.putIfAbsent(channel, nettyChannel);
		}
		return nettyChannel;
	}

	static void removeChannelIfNecessary(Channel ch) {
		if (ch != null && !ch.isActive()) {
			channelMap.remove(ch);
		}
	}

	@Override
	public void send(Object msg) {
		ChannelFuture cf = channel.writeAndFlush(msg);
		boolean success = true;
		int timeout = 1000;
		// long startM = System.currentTimeMillis();
		try {
			success = cf.await(timeout);
			// System.out.println("netty send " + (System.currentTimeMillis() -
			// startM));
		} catch (Throwable e) {
			throw new RuntimeException("send timeout", e);
		}
		if (!success) {
			throw new RuntimeException("send timeout");
		}
	}

	@Override
	public void close() {
		channel.close();
	}

}
