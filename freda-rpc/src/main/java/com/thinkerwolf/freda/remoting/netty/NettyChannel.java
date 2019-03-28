package com.thinkerwolf.freda.remoting.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
//import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.thinkerwolf.freda.common.Net;
import com.thinkerwolf.freda.remoting.Remoting;

public class NettyChannel implements com.thinkerwolf.freda.remoting.Channel {

	private static final ConcurrentMap<Channel, NettyChannel> channelMap = new ConcurrentHashMap<>();
	private Channel channel;
	private Net net;

	NettyChannel(Channel channel, Net net) {
		this.channel = channel;
		this.net = net;
	}

	static NettyChannel getOrAddChannel(Channel channel, Remoting remoting) {
		NettyChannel nettyChannel = channelMap.get(channel);
		if (nettyChannel == null) {
			InetSocketAddress address = (InetSocketAddress) channel.remoteAddress();
			if (address == null) {
				address = (InetSocketAddress) channel.localAddress();
			}
			Net remotingNet = remoting.config();
			Net net = null;
			if (address != null) {
				net = new Net(address.getHostName(), address.getPort(), remotingNet.getProtocol(), remotingNet.getTimeout(), remotingNet.getSerialization());
			} else {
				net = remotingNet;
			}
			nettyChannel = new NettyChannel(channel, net);
			NettyChannel oldChannel = channelMap.putIfAbsent(channel, nettyChannel);
			if (oldChannel != null) {
				nettyChannel = oldChannel;
			}
		}
		if (!channel.isOpen()) {
			channelMap.remove(channel);
		}
		return nettyChannel;
	}
	
	/*static NettyChannel getOrAddChannel(Channel channel, Remoting remoting) {
		NettyChannel nettyChannel = channelMap.get(channel);
		if (nettyChannel == null) {
			InetSocketAddress address = (InetSocketAddress) channel.remoteAddress();
			if (address == null) {
				address = (InetSocketAddress) channel.localAddress();
			}
			Net remotingNet = remoting.config();
			Net net = new Net(address.getHostName(), address.getPort(), remotingNet.getProtocol(), remotingNet.getTimeout(), remotingNet.getSerialization());
			nettyChannel = new NettyChannel(channel, net);
			channelMap.putIfAbsent(channel, nettyChannel);
		}
		return nettyChannel;
	}*/
	
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

	@Override
	public Net net() {
		return net;
	}

	@Override
	public boolean isOpen() {
		return channel.isOpen();
	}

	@Override
	public SocketAddress remoteAddress() {
		return channel.remoteAddress();
	}

}
