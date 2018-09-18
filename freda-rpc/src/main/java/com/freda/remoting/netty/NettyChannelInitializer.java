package com.freda.remoting.netty;

import com.freda.common.ServiceLoader;
import com.freda.remoting.Channels;
import com.freda.remoting.Remoting;
import com.freda.remoting.RemotingClient;
import com.freda.serialization.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;

class NettyChannelInitializer extends ChannelInitializer<Channel> {

    private Remoting remoting;

    public NettyChannelInitializer(Remoting remoting) {
        this.remoting = remoting;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();
        p.addLast("encoder", new InnerEncoder(remoting.config().getSerialization()));
        p.addLast("decoder", new InnerDecoder(remoting.config().getSerialization()));
        p.addLast("handler", new InnerHandler());
    }

    static class InnerEncoder extends MessageToByteEncoder<Object> {
        private String serialization;

        public InnerEncoder(String serialization) {
            this.serialization = serialization;
        }

        @Override
        protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
            Serializer serializer = ServiceLoader.getService(serialization, Serializer.class);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutput oo = serializer.serialize(baos);
            oo.writeObject(msg);
            byte[] msgBytes = baos.toByteArray();
            out.writeInt(msgBytes.length);
            out.writeBytes(msgBytes);
        }
    }

    static class InnerDecoder extends ByteToMessageDecoder {
        private String serialization;

        public InnerDecoder(String serialization) {
            this.serialization = serialization;
        }

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
            byte[] bytes = new byte[dataLen];
            in.readBytes(bytes);
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            Serializer serializer = ServiceLoader.getService(serialization, Serializer.class);
            ObjectInput oi = serializer.deserialize(bais);
            out.add(oi.readObject());
        }
    }

    class InnerHandler extends SimpleChannelInboundHandler<Object> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (remoting instanceof RemotingClient) {
                remoting.handler().received(remoting, msg);
            } else {
                remoting.handler().received(new NettyClient(remoting.config(), Channels.newNettyChannel(ctx.channel())),
                        msg);
            }
        }
    }

}
