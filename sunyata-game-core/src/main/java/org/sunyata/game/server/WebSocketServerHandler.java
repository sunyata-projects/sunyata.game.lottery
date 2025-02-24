/*
 * Copyright 2014 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.sunyata.game.server;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunyata.game.server.message.OctopusInRawMessage;

import java.net.InetSocketAddress;

/**
 * Handles handshakes and messages
 */
public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {

    Logger logger = LoggerFactory.getLogger(WebSocketServerHandler.class);
    private OctopusMsgHandler handler;

    //public static final AttributeKey<String> KEY_USER_ID = AttributeKey.newInstance("USER_ID");
    private static final AttributeKey<Session> SESSION = AttributeKey.valueOf("RmiClientHandler.SESSION");

    public WebSocketServerHandler(OctopusMsgHandler handler) {

        this.handler = handler;
    }

    private static final String WEBSOCKET_PATH = "/websocket";
    private WebSocketServerHandshaker handshaker;

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof FullHttpRequest) {
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {
            try {
                handleWebSocketFrame(ctx, (WebSocketFrame) msg);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

//    private void checkToken(FullHttpRequest request) {
//        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.uri());
//        Map<String, List<String>> parameters = queryStringDecoder.parameters();
//        String channelId = parameters.get("channelId").get(0);
//        String accoundId = parameters.get("accoundId").get(0);
//        String timeStamp = parameters.get("timeStamp").get(0);
//        String sign = parameters.get("sign").get(0);
//    }

    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
        // Handle a bad request.
        if (!req.decoderResult().isSuccess()) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus
                    .BAD_REQUEST));
            return;
        }

        // Allow only GET methods.
        if (req.method() != HttpMethod.GET) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN));
            return;
        }

        // Send the demo page and favicon.ico
        if ("/".equals(req.uri())) {
            ByteBuf content = WebSocketServerBenchmarkPage.getContent(getWebSocketLocation(req));
            FullHttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);

            res.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
            HttpUtil.setContentLength(res, content.readableBytes());
            //HttpHeaders.setContentLength(res, content.readableBytes());

            sendHttpResponse(ctx, req, res);
            return;
        }
        if ("/favicon.ico".equals(req.uri())) {
            FullHttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
            sendHttpResponse(ctx, req, res);
            return;
        }

        // Handshake
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                getWebSocketLocation(req), null, true, 5 * 1024 * 1024);
        handshaker = wsFactory.newHandshaker(req);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handshaker.handshake(ctx.channel(), req);
        }
    }

    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {

        // Check for closing frame
        if (frame instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            System.out.println("关闭握手！");
            return;
        }
        if (frame instanceof PingWebSocketFrame) {
            ctx.write(new PongWebSocketFrame(frame.content().retain()));
            System.out.println("ping ....");
            return;
        }
        if (frame instanceof BinaryWebSocketFrame) {
            // Echo the frame
            BinaryWebSocketFrame binary = (BinaryWebSocketFrame) frame;
            ByteBuf buf = binary.content();
            while (decideFullPackage(20, buf)) {
                OctopusInRawMessage msg = new OctopusInRawMessage();
//                msg.setCmd(buf.readInt());//4
//                msg.setSerial(buf.readInt());//4
//                msg.setVersion(buf.readFloat());//4
//                msg.setDestServerId(buf.readInt());//4
//                msg.setLength(buf.readInt());//4
//                int length = msg.getLength();
//                //buf.readInt();
//                byte[] bytes = new byte[length];
//                if (length != 0) {
//                    buf.readBytes(bytes, 0, length);
//                }
//                msg.setBody(bytes);
                msg.fromByteBuf(buf);
                buf.discardReadBytes();
                logger.info("消息接收解码完成->cmd:{}", msg.getCmd());
                ctx.fireChannelRead(msg);
                //handler.onMessage(msg);

            }
            return;
        }
        if (frame instanceof TextWebSocketFrame) {
            // Echo the frame
            return;
        }
    }

    private boolean decideFullPackage(int headSize, ByteBuf buf) {
        // TODO Auto-generated method stub
        if (buf.capacity() >= headSize) {
            int length = buf.getInt(16);
            int total = buf.writerIndex() - buf.readerIndex();
            if (total - 20 >= length) {
                return true;
            }
        }
        return false;
    }

    private static void sendHttpResponse(
            ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse res) {
        // Generate an error page if response getStatus code is not OK (200).
        if (res.status().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
            HttpUtil.setContentLength(res, res.content().readableBytes());
            //HttpHeaders.setContentLength(res, res.content().readableBytes());
        }

        // Send the response and close the connection if necessary.
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        //HttpUtil.isKeepAlive()
        if (!HttpUtil.isKeepAlive(req) || res.status().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        this.handler.onException((Session) ctx.channel().attr(SESSION).get(), cause);
        InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIp = insocket.getAddress().getHostAddress();
        logger.error("A real IP is detected: " + clientIp);
        if (cause.getMessage().startsWith("Connection reset") //判断是否是RST包的导致的异常
                //因为4层TCP来源IP为客户端的IP，所以若是10、100开头的IP那么肯定是SLB的健康检查IP，这里为了节约篇幅直接判断是否是10或100开头，实际应用中可以详尽的根据上文中的健康检查IP来判断
                && (clientIp.startsWith("10.") || clientIp.startsWith("100."))) {
            logger.info("health check!");
        } else {
            cause.printStackTrace();
            ctx.close();
        }
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        if (logger.isDebugEnabled()) {
            logger.debug("连接成功{}", channel);
        }

//        Session session = this.handler.createSession(ctx);
//        ctx.channel().attr(SESSION).set(session);
//        this.handler.onConnect(session);
        super.channelActive(ctx);
    }

    private static String getWebSocketLocation(FullHttpRequest req) {
        String location = req.headers().get(HttpHeaderNames.HOST) + WEBSOCKET_PATH;
        if (false) {
            return "wss://" + location;
        } else {
            return "ws://" + location;
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("连接断开{}", ctx);
        }

        this.handler.onDisconnect((Session) ctx.channel().attr(SESSION).get());
        ctx.channel().attr(SESSION).set(null);
        super.channelInactive(ctx);
    }
}
