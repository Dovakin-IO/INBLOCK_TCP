package com.bibao.server.nio;

import com.bibao.handler.TickerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.sctp.nio.NioSctpServerChannel;

/**
 * Created by liuhuanchao on 2018/3/15.
 */
public class NioServer {

    private static final int native_port = 8848;

    public static void main(String[] args){

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try{
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup,workerGroup);

            bootstrap.channel(NioSctpServerChannel.class);

            bootstrap.childHandler(new TickerInitializer());

            ChannelFuture future = bootstrap.bind(native_port).sync();
            future.channel().closeFuture().sync();

        } catch (Exception e){

        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
