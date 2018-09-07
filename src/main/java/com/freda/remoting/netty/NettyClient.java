package com.freda.remoting.netty;

import com.freda.common.conf.NetConfig;
import com.freda.remoting.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyClient extends RemotingClient {

	//private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);

	private Bootstrap bootstrap;
	private ChannelFuture startFuture;
	private EventLoopGroup bossGroup;

	public NettyClient(NetConfig conf, Channel channel) {
		super(conf, null);
		this.channel = channel;
	}

	public NettyClient(NetConfig conf, RemotingHandler handler) {
		super(conf, handler);
	}

	@Override
	protected void doInit() {
		this.bootstrap = new Bootstrap();
		this.bossGroup = new NioEventLoopGroup(conf.getBossThreads());
		bootstrap.group(bossGroup);
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.option(ChannelOption.SO_KEEPALIVE, true).option(ChannelOption.TCP_NODELAY, true)
				.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
		bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000);
		NettyChannelInitializer initializer = new NettyChannelInitializer(this);
		bootstrap.handler(initializer);
	}

	@Override
	protected Channel doConnect() {
		try {
			startFuture = bootstrap.connect(conf.getIp(), conf.getPort()).sync();
			boolean sent = startFuture.awaitUninterruptibly(3000);
			if (sent && startFuture.isSuccess()) {
				return NettyChannel.getOrAddChannel(startFuture.channel());
			} else if (startFuture.cause() != null) {
				throw new RemotingException("connect exception occor", startFuture.cause());
			} else {
				throw new RemotingException("connect timeout ");
			}

		} catch (InterruptedException e) {
			throw new RemotingException("netty client start error", e);
		}
	}

	@Override
	public void stop() {
		channel.close();
		bossGroup.shutdownGracefully();
	}

	@Override
	public Channel channel() {
		return super.channel();
	}

}
