package com.freda.remoting.netty;

import com.freda.common.conf.NetConfig;
import com.freda.registry.Server;
import com.freda.remoting.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyClient extends RemotingClient {

    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);

    private Bootstrap bootstrap;
    private Object lock = new Object();
    private ChannelFuture startFuture;

    public NettyClient(NetConfig conf, Channel channel) {
        super(conf);
        this.channel = channel;
    }

    public NettyClient(NetConfig conf) {
        super(conf);
    }

    public NettyClient(NetConfig conf, RemotingHandler handler) {
        super(conf, handler);
    }

    private void doInit() {

        EventLoopGroup bossGroup = new NioEventLoopGroup(conf.getBossThreads());
        try {
            bootstrap = new Bootstrap();
            bootstrap.group(bossGroup);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true)
            //.option(ChannelOption.SO_TIMEOUT, 2000)
            ;
            NettyChannelInitializer initializer = new NettyChannelInitializer(this);
            bootstrap.handler(initializer);
        } catch (Exception e) {
            bossGroup.shutdownGracefully();
            logger.error(e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T sendSync(final Class<T> clazz) {
        return (T) handler.send(this, clazz);
    }

    @Override
    public void invokeAsync() {
    }

    @Override
    public void start() {
        synchronized (lock) {
            if (started) {
                return;
            }
            try {
                doInit();
                String host = null;
                int port = 0;
                if (StringUtils.isNotEmpty(conf.getIp()) && conf.getPort() > 0) {
                    host = conf.getIp();
                    port = conf.getPort();
                } else {
                    Server server = null;
                    try {
                        server = getRegistry().getRandomServer(conf.getProtocol());
                        if (server == null) {
                            throw new RuntimeException("server null");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException("server null");
                    }
                    host = server.getHost();
                    port = server.getPort();
                    this.conf = conf.clone();
                    this.conf.setIp(host);
                    this.conf.setPort(port);
                }
                startFuture = bootstrap.connect(host, port).sync();
                this.channel = Channels.newNettyChannel(startFuture.channel());
                final String debugStr = host + ":" + port;
                startFuture.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (future.isSuccess()) {
                            if (logger.isDebugEnabled()) {
                                logger.debug(new StringBuilder("connect to [").append(debugStr).append("] success!")
                                        .toString());
                            }
                            started = true;
                        }
                    }
                });
            } catch (InterruptedException e) {
                throw new RemotingException("netty client start error", e);
            }
        }
    }

    @Override
    public void stop() {
        channel.close();
    }
}
