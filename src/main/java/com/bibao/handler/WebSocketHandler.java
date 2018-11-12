package com.bibao.handler;

import com.bibao.cache.Global;
import com.bibao.protocol.RequestType;
import com.bibao.server.Session;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.omg.CORBA.Request;

/**
 * Created by liuhuanchao on 2018/3/17.
 */
public class WebSocketHandler extends SimpleChannelInboundHandler<Object> {

    static Logger logger = LogManager.getLogger(WebSocketHandler.class.getName());

    private WebSocketServerHandshaker handshaker;
    private static final String WEBSOCKET_URL = "ws://localhost:11750/websocket";

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {

        // 处理HTTP请求
        if(o instanceof FullHttpRequest){
            handleHttpRequest(channelHandlerContext, (FullHttpRequest) o);
        }
        // WebSocket
        else if(o instanceof WebSocketFrame){
            handleWebSocketFrame(channelHandlerContext, (WebSocketFrame) o);
        }
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest request){
        // 过滤非法请求,以及非WebSocket握手请求
        if(!request.decoderResult().isSuccess()
                || !("websocket".equals(request.headers().get("Upgrade")))){
            logger.debug("非法请求");
            sendHttpResponse(ctx, request,
                    new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }

        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                WEBSOCKET_URL,
                null,
                false
        );
        handshaker = wsFactory.newHandshaker(request);

        // 过滤服务不支持的协议版本请求
        if (null == handshaker) {
            logger.debug("不支持的协议类型");
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
            return;
        }

        handshaker.handshake(ctx.channel(), request);
    }

    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame){
        // close
        if(frame instanceof CloseWebSocketFrame){
            Global.session.remove(ctx.channel().id());
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }
        if(frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }

        // 暂时只支持文本格式的数据
        if(!(frame instanceof TextWebSocketFrame)){
            return;
        }

        String request = ((TextWebSocketFrame) frame).text();
        if(varifyRequest(request)){
            if(!Global.isExist(ctx.channel().id())){
                Session session = new Session(ctx.channel(), request);
                session.updateTopic(request);
                Global.session.put(ctx.channel().id(), session);
                logger.debug("新的session已创立");
            } else {
                Global.session.get(ctx.channel().id()).updateTopic(request);
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Global.server.ALL_CHANNELS.add(ctx.channel());
        logger.debug("新的客户端连入...[address: " + ctx.channel().localAddress().toString() + "]");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Global.server.ALL_CHANNELS.remove(ctx.channel());
        Global.session.remove(ctx.channel().id());
        logger.debug("客户端断开...[address: " + ctx.channel().localAddress().toString() + "]");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("连接异常...[address: " + ctx.channel().localAddress().toString() + "]");
        ctx.close();
    }

    /**
     * 验证WebSocket请求
     * @param request
     * @return
     */
    private boolean varifyRequest(String request){
        if(null == request || request.equals("")) return false;

        for(RequestType type : RequestType.values()){
            if (type.getName().equals(request)) return true;
        }
        return false;
    }

    /**
     * 响应请求
     * @param ctx
     * @param req
     * @param res
     */
    private void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req,
                                  DefaultFullHttpResponse res) {

        // 返回应答给客户端
        if (res.status().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
        }

        // 如果是非Keep-Alive，关闭连接
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        if (res.status().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

}
