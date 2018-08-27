package com.freda.test;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpHeaders.getHost;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class NettyHttpServer {
    public static void main(String[] args) {

        NioEventLoopGroup boss = new NioEventLoopGroup(1);
        NioEventLoopGroup worker = new NioEventLoopGroup(2);
        ServerBootstrap sb = new ServerBootstrap();
        sb.group(boss, worker);
        sb.channel(NioServerSocketChannel.class);
        sb.childHandler(new ChannelInitializer<Channel>() {

            @Override
            protected void initChannel(Channel ch) throws Exception {
                ch.pipeline().addLast(new HttpRequestDecoder());
                ch.pipeline().addLast(new HttpResponseEncoder());
                ch.pipeline().addLast(new HttpServerHandler());
            }
        });
        ChannelFuture cf = sb.bind(new InetSocketAddress("127.0.0.1", 8080));
    }

    static class HttpServerHandler extends SimpleChannelInboundHandler<Object> {

        /**
         * Buffer that stores the response content
         */
        private final StringBuilder buf = new StringBuilder();
        private HttpRequest request;

        private static void appendDecoderResult(StringBuilder buf, HttpObject o) {
            DecoderResult result = o.getDecoderResult();
            if (result.isSuccess()) {
                return;
            }

            buf.append(".. WITH DECODER FAILURE: ");
            buf.append(result.cause());
            buf.append("\r\n");
        }

        private static void send100Continue(ChannelHandlerContext ctx) {
            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, CONTINUE);
            ctx.write(response);
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            ctx.flush();
        }

        @SuppressWarnings("deprecation")
        private boolean writeResponse(HttpObject currentObj, ChannelHandlerContext ctx) {
            // Decide whether to close the connection or not.
            boolean keepAlive = HttpUtil.isKeepAlive(request);
            // Build the response object.
            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1,
                    currentObj.decoderResult().isSuccess() ? OK : BAD_REQUEST,
                    Unpooled.copiedBuffer(buf.toString(), CharsetUtil.UTF_8));

            response.headers().set("Content-Type", "text/plain; charset=UTF-8");

            if (keepAlive) {
                // Add 'Content-Length' header only for a keep-alive connection.
                response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
                // Add keep alive header as per:
                // -
                // http://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01.html#Connection
                response.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            }

            // Encode the cookie.
            String cookieString = request.headers().get(COOKIE);
            if (cookieString != null) {
                Set<Cookie> cookies = CookieDecoder.decode(cookieString);
                if (!cookies.isEmpty()) {
                    // Reset the cookies if necessary.
                    for (Cookie cookie : cookies) {
                        response.headers().add(SET_COOKIE, ServerCookieEncoder.encode(cookie));
                    }
                }
            } else {
                // Browser sent no cookie. Add some.
                response.headers().add(SET_COOKIE, ServerCookieEncoder.encode("key1", "value1"));
                response.headers().add(SET_COOKIE, ServerCookieEncoder.encode("key2", "value2"));
            }

            // Write the response.
            ctx.write(response);

            return keepAlive;
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }

        @SuppressWarnings("deprecation")
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (msg instanceof HttpRequest) {
                HttpRequest request = this.request = (HttpRequest) msg;

                if (HttpUtil.is100ContinueExpected(request)) {
                    send100Continue(ctx);
                }

                buf.setLength(0);
                buf.append("WELCOME TO THE WILD WILD WEB SERVER\r\n");
                buf.append("===================================\r\n");

                buf.append("VERSION: ").append(request.protocolVersion()).append("\r\n");
                buf.append("HOSTNAME: ").append(getHost(request, "unknown")).append("\r\n");
                buf.append("REQUEST_URI: ").append(request.getUri()).append("\r\n\r\n");

                HttpHeaders headers = request.headers();
                if (!headers.isEmpty()) {
                    for (Map.Entry<String, String> h : headers) {
                        String key = h.getKey();
                        String value = h.getValue();
                        buf.append("HEADER: ").append(key).append(" = ").append(value).append("\r\n");
                    }
                    buf.append("\r\n");
                }

                QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.getUri());
                Map<String, List<String>> params = queryStringDecoder.parameters();
                if (!params.isEmpty()) {
                    for (Entry<String, List<String>> p : params.entrySet()) {
                        String key = p.getKey();
                        List<String> vals = p.getValue();
                        for (String val : vals) {
                            buf.append("PARAM: ").append(key).append(" = ").append(val).append("\r\n");
                        }
                    }
                    buf.append("\r\n");
                }

                appendDecoderResult(buf, request);
            }

            if (msg instanceof HttpContent) {
                HttpContent httpContent = (HttpContent) msg;

                ByteBuf content = httpContent.content();
                if (content.isReadable()) {
                    buf.append("CONTENT: ");
                    buf.append(content.toString(CharsetUtil.UTF_8));
                    buf.append("\r\n");
                    appendDecoderResult(buf, request);
                }

                if (msg instanceof LastHttpContent) {
                    buf.append("END OF CONTENT\r\n");

                    LastHttpContent trailer = (LastHttpContent) msg;
                    if (!trailer.trailingHeaders().isEmpty()) {
                        buf.append("\r\n");
                        for (String name : trailer.trailingHeaders().names()) {
                            for (String value : trailer.trailingHeaders().getAll(name)) {
                                buf.append("TRAILING HEADER: ");
                                buf.append(name).append(" = ").append(value).append("\r\n");
                            }
                        }
                        buf.append("\r\n");
                    }

                    writeResponse(trailer, ctx);
                }
            }

        }

    }

}
