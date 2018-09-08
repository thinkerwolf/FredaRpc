package com.freda.remoting.netty;

import com.freda.remoting.Channels;
import com.freda.remoting.Remoting;
import com.freda.remoting.RemotingClient;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

class NettyChannelInitializer extends ChannelInitializer<Channel> {

	private Remoting remoting;

	public NettyChannelInitializer(Remoting remoting) {
		this.remoting = remoting;
	}

	@Override
	protected void initChannel(Channel ch) throws Exception {
		ChannelPipeline p = ch.pipeline();
		p.addLast("encoder", new InnerEncoder());
		p.addLast("decoder", new InnerDecoder());
		p.addLast("handler", new InnerHandler());
	}

	static class InnerEncoder extends MessageToByteEncoder<Object> {
		@Override
		protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
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

	static class InnerDecoder extends ByteToMessageDecoder {
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
			byte[] b = new byte[dataLen];
			in.readBytes(b);
			ByteArrayInputStream bais = new ByteArrayInputStream(b);
			ObjectInputStream ois = new ObjectInputStream(bais);
			Object obj = ois.readObject();
			bais.close();
			ois.close();
			out.add(obj);
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
