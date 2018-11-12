package com.bibao.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * Created by liuhuanchao on 2018/3/15.
 */
public class TickerInitializer extends ChannelInitializer<SocketChannel> {

    private static final int MAX_IDLE_SECONDS = 60;

    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast("idleStateCheck", new IdleStateHandler(MAX_IDLE_SECONDS,
                MAX_IDLE_SECONDS, MAX_IDLE_SECONDS));
        pipeline.addLast("http-codec", new HttpServerCodec());
        pipeline.addLast("aggregator", new HttpObjectAggregator(65535));
        pipeline.addLast("http-chunked", new ChunkedWriteHandler());
        pipeline.addLast("business", new WebSocketHandler());
    }
}
