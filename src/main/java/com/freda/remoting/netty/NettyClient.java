package com.freda.remoting.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.Signal;
import io.netty.util.concurrent.DefaultPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.freda.registry.Server;
import com.freda.remoting.RemotingClient;
import com.freda.remoting.RemotingException;
import com.freda.remoting.RequestMessage;
import com.freda.remoting.ResponseMessage;
import com.freda.common.conf.NettyConfig;
import com.freda.common.proxy.ProxyHandler;
import com.freda.common.util.ProxyUtils;
import com.freda.config.ReferenceConfig;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class NettyClient extends RemotingClient {

	private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);
	private static final AtomicInteger THREAD_NUM = new AtomicInteger();
	private static final AtomicInteger ID_GENARETOR = new AtomicInteger();
	
	private Bootstrap bootstrap;
	private BlockingQueue<ResponseMessage> responseMessages = new LinkedBlockingQueue<ResponseMessage>();
	private Map<Integer, ResponseFuture> waitResultMap = new HashMap<Integer, ResponseFuture>();
	private Thread _responseHandleThread;
	private Channel channel;
	private Object lock = new Object();
	private ChannelFuture startFuture;

	public NettyClient(NettyConfig conf) {
		super(conf);
	}

	public void doInit() {
		_responseHandleThread = new Thread(new ResponseHandleTask(),
				"ResponseHandler-Thread-" + THREAD_NUM.incrementAndGet());
		_responseHandleThread.setDaemon(false);
		_responseHandleThread.setPriority(Thread.NORM_PRIORITY);
		_responseHandleThread.start();

		EventLoopGroup bossGroup = new NioEventLoopGroup(conf.getBossThreads());
		try {
			bootstrap = new Bootstrap();
			bootstrap.group(bossGroup);
			bootstrap.channel(NioSocketChannel.class);
			bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
			bootstrap.handler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast("encoder", new ClientEncoder());
					ch.pipeline().addLast("decoder", new ClientDecoder());
					ch.pipeline().addLast("handler", new ClientHandler());
				}
			});
		} catch (Exception e) {
			_responseHandleThread.interrupt();
			bossGroup.shutdownGracefully();
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public <T> T invokeSync(final Class<T> clazz) {
		final ReferenceConfig<T> refConfig = getReferenceConfig(clazz);
		if (refConfig == null) {
			return null;
		}
		if (!startFuture.isDone()) {
			try {
				startFuture.sync();
			} catch (InterruptedException e) {
				// Ingore
				return null;
			}
		}
		if (refConfig.getRef() == null) {
			T ref = ProxyUtils.newProxy(clazz, new ProxyHandler() {
				@Override
				public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
					final int id = ID_GENARETOR.incrementAndGet();
					RequestMessage rm = new RequestMessage();
					rm.setId(id);
					rm.setClazzName(refConfig.getId());
					rm.setMethodName(method.getName());
					rm.setParameterTypes(method.getParameterTypes());
					rm.setArgs(args);
					ResponseFuture rf = new ResponseFuture();
					waitResultMap.put(id, rf);
					ChannelFuture cf = channel.writeAndFlush(rm);
					cf.sync();
					rf.sync();
					return rf.getResult();
				}
			});
			refConfig.setRef(ref);
		}
		return (T) refConfig.getRef();
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
				Server server = null;
				try {
					server = getRegistry().getRandomServer();
				} catch (Exception e) {
					e.printStackTrace();
				}
				final String host = server.getHost();
				final int port = server.getPort();

				startFuture = bootstrap.connect(host, port).sync();
				startFuture.addListener(new ChannelFutureListener() {
					@Override
					public void operationComplete(ChannelFuture future) throws Exception {
						if (future.isSuccess()) {
							if (logger.isDebugEnabled()) {
								logger.debug(new StringBuilder("connect to [").append(host).append(":").append(port)
										.append("] success!").toString());
							}
							started = true;
						}
					}
				});
				this.channel = startFuture.channel();
			} catch (InterruptedException e) {
				throw new RemotingException("netty client start error", e);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T refer(final Class<T> clazz) {
		return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[] { clazz }, new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				final int id = ID_GENARETOR.incrementAndGet();
				RequestMessage rm = new RequestMessage();
				rm.setClazzName(clazz.getSimpleName());
				rm.setId(id);
				rm.setMethodName(method.getName());
				rm.setParameterTypes(method.getParameterTypes());
				rm.setArgs(args);
				ResponseFuture rf = new ResponseFuture();
				waitResultMap.put(id, rf);
				ChannelFuture cf = channel.writeAndFlush(rm);
				cf.sync();
				rf.sync();
				return rf.getResult();
			}
		});
	}

	@SuppressWarnings("unchecked")
	public <T> T refer(final String serviceName, final Class<T> clazz) {
		return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[] { clazz }, new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				final int id = ID_GENARETOR.incrementAndGet();
				RequestMessage rm = new RequestMessage();
				rm.setClazzName(serviceName);
				rm.setId(id);
				rm.setMethodName(method.getName());
				rm.setParameterTypes(method.getParameterTypes());
				rm.setArgs(args);
				ResponseFuture rf = new ResponseFuture();
				waitResultMap.put(id, rf);
				ChannelFuture cf = channel.writeAndFlush(rm);
				cf.sync();
				rf.sync();
				return rf.getResult();
			}
		});
	}

	public void addResponseMessage(ResponseMessage rm) {
		if (rm == null) {
			throw new IllegalArgumentException("can't be null");
		}
		this.responseMessages.offer(rm);
	}

	static class ClientEncoder extends MessageToByteEncoder<RequestMessage> {
		@Override
		protected void encode(ChannelHandlerContext ctx, RequestMessage msg, ByteBuf out) throws Exception {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(msg);
			byte[] msgBytes = baos.toByteArray();
			out.writeInt(msgBytes.length);
			out.writeBytes(msgBytes);
			oos.close();
			baos.close();
		}
	}

	static class ClientDecoder extends ByteToMessageDecoder {
		@Override
		protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
			if (in.readableBytes() < 4) {
				return;
			}
			int dataLen = in.getInt(in.readerIndex());
			if (in.readableBytes() < 4 + dataLen) {
				return;
			}
			in.skipBytes(4);
			byte[] b = new byte[in.readableBytes()];
			in.readBytes(b);
			ByteArrayInputStream bais = new ByteArrayInputStream(b);
			ObjectInputStream ois = new ObjectInputStream(bais);
			Object obj = ois.readObject();
			bais.close();
			ois.close();
			out.add(obj);
		}
	}

	/**
	 * 参考netty {@link DefaultPromise}
	 */
	private static class ResponseFuture {
		private static final Signal SUCCESS = Signal.valueOf(ResponseFuture.class, "SUCCESS");
		private static final Signal UNCANCELLABLE = Signal.valueOf(ResponseFuture.class, "UNCANCELLABLE");
		/**  */
		private Object result;
		private int waiters;
		private Object waitState;

		public ResponseFuture() {
			waitState = UNCANCELLABLE;
			this.waiters = 0;
		}

		public Object getResult() {
			return result;
		}

		public void setResult(Object result) {
			this.result = result;
		}

		public void sync() throws InterruptedException {
			if (isDone()) {
				return;
			}
			if (Thread.interrupted()) {
				throw new InterruptedException("Thread " + Thread.currentThread().getName() + " is interrupted");
			}
			synchronized (this) {
				while (!isDone()) {
					try {
						waiters++;
						wait();
					} finally {
						waiters--;
					}
				}
			}
		}

		public boolean isDone() {
			return this.waitState == SUCCESS;
		}

		public void setSuccess(Object result) {
			setSuccess0(result);
		}

		private void setSuccess0(Object result) {
			this.waitState = result == null ? SUCCESS : result;
			checkNotifyWaiters();
		}

		private synchronized void checkNotifyWaiters() {
			if (waiters > 0) {
				notifyAll();
			}
		}
	}

	// 轮询处理
	private class ResponseHandleTask implements Runnable {
		@Override
		public void run() {
			try {
				while (!Thread.interrupted()) {
					ResponseMessage rm = responseMessages.take();
					ResponseFuture rf = waitResultMap.remove(new Integer(rm.getId()));
					if (rf != null) {
						rf.setResult(rm.getResult());
						rf.setSuccess(null);
					}
				}
				if (logger.isDebugEnabled()) {
					logger.debug("NettyClient is shutdowned");
				}
			} catch (Exception e) {
				if (logger.isDebugEnabled()) {
					logger.debug("NettyClient is shutdowned");
				}
			}
		}
	}

	class ClientHandler extends SimpleChannelInboundHandler<ResponseMessage> {
		@Override
		protected void channelRead0(ChannelHandlerContext ctx, ResponseMessage responseMessage) throws Exception {
			responseMessages.offer(responseMessage);
			ctx.close();
		}
	}

	@Override
	public void stop() {
		_responseHandleThread.interrupt();
		channel.close();
	}
}