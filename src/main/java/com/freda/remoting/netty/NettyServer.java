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

import com.freda.common.conf.Configuration;
import com.freda.common.conf.NettyConfig;
import com.freda.common.conf.ServiceConfig;
import com.freda.registry.Server;
import com.freda.registry.ServerNameBuilder;
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

	public NettyServer(Configuration configuration) {
		super(configuration);
	}

	@Override
	public synchronized void start() {
		if (started) {
			return;
		}
		NettyConfig nettyConfig = configuration.getNettyConfig();
		ServerBootstrap sb = new ServerBootstrap();
		EventLoopGroup fatherLoop = new NioEventLoopGroup(nettyConfig.getBossThreads());
		EventLoopGroup childLoop = new NioEventLoopGroup(nettyConfig.getWorkerThreads());
		sb.group(fatherLoop, childLoop).channel(NioServerSocketChannel.class)
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel socketChannel) throws Exception {
						ChannelPipeline pipeline = socketChannel.pipeline();
						pipeline.addLast("decoder", new InnerDecoder());
						pipeline.addLast("encoder", new InnerEncoder());
						pipeline.addLast("handler", new MessageHandler());
					}
				});
		final String host = nettyConfig.getIp();
		final int port = nettyConfig.getPort();
		final String serverName = ServerNameBuilder.getInstance().generateServerName(SERVER, host, port);

		ChannelFuture cf = sb.bind(new InetSocketAddress(host, port));
		cf.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				if (future.isSuccess()) {
					if (logger.isDebugEnabled()) {
						logger.info(getClass().getSimpleName() + " listen tcp on " + port + " success");
					}
					started = true;
					registry = initRegistry();
					registerSelf(new Server(serverName, host, port));
				}
			}
		});
		Channel channel = cf.channel();
		ChannelFuture closeFuture = channel.closeFuture();
		closeFuture.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				if (registry != null) {
					registry.close();
				}
			}
		});
	}

	static class InnerDecoder extends ByteToMessageDecoder {
		@Override
		protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list)
				throws Exception {
			// 前4字节 消息主体长度。
			try {
				// 消息接收完成校验...
				if (byteBuf.readableBytes() < 4) {
					return;
				}
				int dataLen = byteBuf.getInt(byteBuf.readerIndex());
				if (byteBuf.readableBytes() < dataLen + 4) {
					return;
				}
				byteBuf.skipBytes(4);
				int length = byteBuf.readableBytes();
				byte[] msgBytes = new byte[length];
				byteBuf.readBytes(msgBytes);
				ByteArrayInputStream bais = new ByteArrayInputStream(msgBytes);
				ObjectInputStream ois = new ObjectInputStream(bais);
				RequestMessage message = (RequestMessage) ois.readObject();
				list.add(message);
				bais.close();
				ois.close();
				if (logger.isDebugEnabled()) {
					logger.info("requestMessage : " + message);
				}
			} finally {
				// ReferenceCountUtil.release(byteBuf);
				// byteBuf.clear();
			}

		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
			super.exceptionCaught(ctx, cause);
			logger.error(cause.getMessage(), cause);
		}
	}

	static class InnerEncoder extends MessageToByteEncoder<ResponseMessage> {

		@Override
		protected void encode(ChannelHandlerContext channelHandlerContext, ResponseMessage responseMessage,
				ByteBuf byteBuf) throws Exception {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(responseMessage);
			byte[] bytes = baos.toByteArray();
			byteBuf.writeInt(bytes.length);
			byteBuf.writeBytes(bytes);
			baos.close();
			oos.close();
		}
	}

	class MessageHandler extends SimpleChannelInboundHandler<RequestMessage> {

		public MessageHandler() {
		}

		@Override
		protected void channelRead0(ChannelHandlerContext channelHandlerContext, RequestMessage requestMessage)
				throws Exception {
			ServiceConfig serviceConfig = configuration.getServiceConfig(requestMessage.getClazzName());
			Object obj = serviceConfig.getServiceObj();
			ResponseMessage responseMessage = new ResponseMessage();
			responseMessage.setId(requestMessage.getId());
			if (obj == null) {
				responseMessage.setSuccess(false);
			} else {
				Method method = serviceConfig.getClazz().getMethod(requestMessage.getMethodName(),
						requestMessage.getParameterTypes());
				method.setAccessible(true);
				Object result = method.invoke(obj, requestMessage.getArgs());
				responseMessage.setSuccess(false);
				responseMessage.setResult(result);
			}
			// ReferenceCountUtil.release(requestMessage);
			channelHandlerContext.channel().write(responseMessage);
			channelHandlerContext.channel().flush();
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
			// super.exceptionCaught(ctx, cause);
			logger.error("error", cause);
		}
	}

}
