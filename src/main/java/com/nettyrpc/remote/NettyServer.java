package com.nettyrpc.remote;

import com.nettyrpc.spring.RpcBeanPostProcessor;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class NettyServer {
    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);
    private static final AtomicInteger SERVER_NAME_ID = new AtomicInteger();
    private static final String SERVER = "server";

    private ZooKeeperClient zkClient;

    private String registerAddress;

    private ApplicationContext context;

    private RpcBeanPostProcessor processor;

    public NettyServer() {

    }

    public void doStart(int port) throws Exception {
        if (this.context == null) {
            // this.context = new
            // ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        }

        ServerBootstrap sb = new ServerBootstrap();
        EventLoopGroup fatherLoop = new NioEventLoopGroup(2);
        EventLoopGroup childLoop = new NioEventLoopGroup(10);
        sb.group(fatherLoop, childLoop).channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast("decoder", new InnerDecoder());
                        pipeline.addLast("encoder", new InnerEncoder());
                        pipeline.addLast("handler", new MessageHandler(context));
                    }
                });
        String host = InetAddress.getLocalHost().getHostAddress();
        ChannelFuture cf = sb.bind(new InetSocketAddress(host, port));
        logger.info("listen tcp on " + port + " success");

        Channel channel = cf.channel();
        ChannelFuture closeFuture = channel.closeFuture();
        initRegisterClient();
        String serverName = new StringBuilder(SERVER).append("-").append(SERVER_NAME_ID.incrementAndGet()).toString();
        registerSelf(serverName, new StringBuilder(host).append(":").append(port).toString());
        closeFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (zkClient != null) {
                    zkClient.close();
                }
            }
        });

    }

    public void setRegisterAddress(String registerAddress) {
        this.registerAddress = registerAddress;
    }

    public void setApplicationContext(ApplicationContext context) {
        this.context = context;
    }

    public RpcBeanPostProcessor getProcessor() {
        return processor;
    }

    public void setProcessor(RpcBeanPostProcessor processor) {
        this.processor = processor;
    }

    private void initRegisterClient() {
        if (StringUtils.isNotEmpty(registerAddress)) {
            try {
                zkClient = new ZooKeeperClient(registerAddress, 1000);
            } catch (Exception e) {
                logger.error("ZooKeeper start or register fail!", e);
            }
        }
    }

    private void registerSelf(String serverName, String address) {
        if (zkClient != null) {
            try {
                zkClient.register(serverName, address);
            } catch (Exception e) {
                logger.error("Register ZooKeeper fail!", e);
            }
        }

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

        private ApplicationContext context;

        public MessageHandler(ApplicationContext context) {
            this.context = context;
        }

        @Override
        protected void channelRead0(ChannelHandlerContext channelHandlerContext, RequestMessage requestMessage)
                throws Exception {
            Object obj = null;
            if (processor != null) {
                obj = processor.refer(requestMessage.getClazzName());
            }
            if (obj == null) {
                obj = context.getBean(requestMessage.getClazzName());
            }
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setId(requestMessage.getId());
            if (obj == null) {
                responseMessage.setSuccess(false);
            } else {
                Method method = obj.getClass().getMethod(requestMessage.getMethodName(),
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
