package com.freda.remoting.netty;

import com.freda.common.Configuration;
import com.freda.common.Constants;
import com.freda.common.Net;
import com.freda.remoting.RemotingHandler;
import com.freda.remoting.RemotingServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class NettyServer extends RemotingServer {

	private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

	private static final int bossThreads = Math.max(1,
			Configuration.getIntProperty(Configuration.NETTY_BOSS_THREADS, Constants.DEFAULT_THREAD_NUM / 2));
	private static final int workerThreads = Math.max(1,
			Configuration.getIntProperty(Configuration.NETTY_WORKER_THREADS, Constants.DEFAULT_THREAD_NUM));
	
	private ServerBootstrap serverBootstrap;
	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;
	private Channel channel;

	public NettyServer(Net conf, RemotingHandler handler) {
        super(conf, handler);
        this.serverBootstrap = new ServerBootstrap();
        this.bossGroup = new NioEventLoopGroup(bossThreads);
        this.workerGroup = new NioEventLoopGroup(workerThreads);
    }

	@Override
	public void stop() {
		channel.close();
	}

	@Override
	protected com.freda.remoting.Channel doBind() {
		serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class);
		NettyChannelInitializer initializer = new NettyChannelInitializer(this);
		serverBootstrap.childHandler(initializer);
		final String host = conf.getHost();
		final int port = conf.getPort();
		// final String serverName =
		// ServerNameBuilder.getInstance().generateServerName(SERVER, host,
		// port);
		ChannelFuture cf = serverBootstrap.bind(new InetSocketAddress(host, port));
		cf.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				if (future.isSuccess()) {
					if (logger.isDebugEnabled()) {
						logger.info(getClass().getSimpleName() + " listen tcp on " + port + " success");
					}
					// registerSelf(new Server(serverName, host, port,
					// conf.getProtocol()));
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
		return NettyChannel.getOrAddChannel(channel);
	}

}
