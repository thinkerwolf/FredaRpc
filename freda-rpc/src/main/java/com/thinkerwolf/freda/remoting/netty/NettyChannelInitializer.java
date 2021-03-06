package com.thinkerwolf.freda.remoting.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.concurrent.EventExecutor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

import com.thinkerwolf.freda.common.ServiceLoader;
import com.thinkerwolf.freda.remoting.Remoting;
import com.thinkerwolf.freda.serialization.ObjectInput;
import com.thinkerwolf.freda.serialization.ObjectOutput;
import com.thinkerwolf.freda.serialization.Serializer;

class NettyChannelInitializer extends ChannelInitializer<Channel> {

	private Remoting remoting;

	public NettyChannelInitializer(Remoting remoting) {
		this.remoting = remoting;
	}

	@Override
	protected void initChannel(Channel ch) throws Exception {
		ChannelPipeline p = ch.pipeline();
		String seialization = remoting.config().getSerialization();
		p.addLast("encoder", new InnerEncoder(seialization));
		p.addLast("decoder", new InnerDecoder(seialization, remoting.handler().decodeClass()));
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
			oo.close();
			byte[] msgBytes = baos.toByteArray();
			out.writeInt(msgBytes.length);
			out.writeBytes(msgBytes);
		}
	}

	static class InnerDecoder extends ByteToMessageDecoder {
		private String serialization;
		private Class<?> decodeClass;

		public InnerDecoder(String serialization, Class<?> decodeClass) {
			this.serialization = serialization;
			this.decodeClass = decodeClass;
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
			out.add(oi.readObject(decodeClass));
			oi.close();
		}
	}

	class InnerHandler extends SimpleChannelInboundHandler<Object> {
		@Override
		protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
			Channel ch = ctx.channel();
			remoting.handler().received(NettyChannel.getOrAddChannel(ch, remoting), msg);
		}
	}

}
