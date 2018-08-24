package com.freda.remoting.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.freda.common.conf.NetConfig;
import com.freda.config.ServiceConfig;
import com.freda.registry.Server;
import com.freda.registry.ServerNameBuilder;
import com.freda.remoting.RemotingHandler;
import com.freda.remoting.RemotingServer;
import com.freda.remoting.RequestMessage;
import com.freda.remoting.ResponseMessage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.List;

public class NettyServer extends RemotingServer {

	private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);
	private static final String SERVER = "server";

	private ServerBootstrap serverBootstrap;

	private Channel channel;

	public NettyServer(NetConfig conf) {
		super(conf);
	}

	public NettyServer(NetConfig conf, RemotingHandler handler) {
		super(conf, handler);
	}

	@Override
	public synchronized void start() {
		if (started) {
			return;
		}
		final EventLoopGroup bossGroup = new NioEventLoopGroup(conf.getBossThreads());
		final EventLoopGroup workerGroup = new NioEventLoopGroup(conf.getWorkerThreads());
		serverBootstrap = new ServerBootstrap();
		serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class);
		NettyChannelInitializer initializer = new NettyChannelInitializer(this);
		serverBootstrap.childHandler(initializer);
		final String host = conf.getIp();
		final int port = conf.getPort();
		final String serverName = ServerNameBuilder.getInstance().generateServerName(SERVER, host, port);

		ChannelFuture cf = serverBootstrap.bind(new InetSocketAddress(host, port));
		cf.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				if (future.isSuccess()) {
					if (logger.isDebugEnabled()) {
						logger.info(getClass().getSimpleName() + " listen tcp on " + port + " success");
					}
					started = true;
					registerSelf(new Server(serverName, host, port, conf.getProtocol()));
				}
			}
		});
		channel = cf.channel();
		ChannelFuture closeFuture = channel.closeFuture();
		closeFuture.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				bossGroup.shutdownGracefully();
				workerGroup.shutdownGracefully();
			}
		});
	}

	@Override
	public void stop() {
		channel.close();
	}

}
