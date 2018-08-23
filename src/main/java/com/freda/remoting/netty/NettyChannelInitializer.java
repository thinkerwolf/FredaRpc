package com.freda.remoting.netty;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.util.List;

import com.freda.common.conf.NettyConfig;
import com.freda.remoting.Channels;
import com.freda.remoting.Remoting;
import com.freda.remoting.RemotingClient;
import com.freda.remoting.RemotingServer;
import com.freda.remoting.RequestMessage;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.ClientCookieEncoder;
import io.netty.handler.codec.http.DefaultCookie;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpVersion;

public class NettyChannelInitializer extends ChannelInitializer<Channel> {

	private Remoting remoting;

	private boolean server;

	public NettyChannelInitializer(Remoting remoting) {
		this.remoting = remoting;
		this.server = remoting instanceof RemotingServer ? true : false;
	}

	@Override
	protected void initChannel(Channel ch) throws Exception {
		NettyConfig nc = remoting.config();
		String protocol = nc.getProtocol();
		
		ch.pipeline().addLast("encoder", new InnerEncoder());
		
		// 根据不同协议新建Decoder
		if ("freda".equals(protocol)) {
			//ch.pipeline().addLast(new Http);
		} else if ("http".equals(protocol)) {
			ch.pipeline().addLast(new HttpRequestAppender(new URI("http:")));
			ch.pipeline().addLast(new HttpRequestEncoder());
		}
		
		ch.pipeline().addLast("decoder", new InnerDecoder());
		ch.pipeline().addLast("handler", new ClientHandler());
	}
	
	static class  HttpRequestAppender extends MessageToMessageEncoder<ByteBuf> {
		
		private URI uri;
		public HttpRequestAppender(URI uri) {
			this.uri = uri;
		}

		@Override
		protected void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
			 // Prepare the HTTP request.
			String host = uri.getHost() == null? "localhost" : uri.getHost();
			
			//HttpPost
            HttpRequest request = new DefaultFullHttpRequest(
                    HttpVersion.HTTP_1_1, HttpMethod.GET, uri.getRawPath(), msg);
            request.headers().set(HttpHeaderNames.HOST, host);
            request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
            request.headers().set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP);
            
            // Set some example cookies.
            request.headers().set(
            		HttpHeaderNames.COOKIE,
                    ClientCookieEncoder.encode(
                            new DefaultCookie("my-cookie", "foo"),
                            new DefaultCookie("another-cookie", "bar")));
            out.add(request);
		}
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

	class ClientHandler extends SimpleChannelInboundHandler<Object> {
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
