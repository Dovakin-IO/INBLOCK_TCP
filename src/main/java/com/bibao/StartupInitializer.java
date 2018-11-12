//package com.bibao;
//
//import com.bibao.factory.NamedThreadFactory;
//import com.bibao.handler.TickerInitializer;
//import com.bibao.server.AbstractNettyServer;
//import com.bibao.server.NettyConfig;
//import io.netty.channel.Channel;
//import io.netty.channel.ChannelInitializer;
//import io.netty.channel.ChannelOption;
//import io.netty.channel.nio.NioEventLoopGroup;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * Created by liuhuanchao on 2018/3/16.
// */
//public class StartupInitializer {
//
//    private static final int portNumber = 11700;
//
//    public AbstractNettyServer getServer(){
////        NettyTCPServer server = new NettyTCPServer(getConfig(), getChannelInitializer());
////
////        return server;
//    }
//
//    private NettyConfig getConfig(){
//        NettyConfig config = new NettyConfig();
//        config.setPortNumber(portNumber);
//        config.setChannelOptions(getChannelOptions());
//        config.setBossGroup(getBossGroup());
//        config.setWorkerGroup(getWorkerGroup());
//        return config;
//    }
//
//    private ChannelInitializer<? extends Channel> getChannelInitializer(){
//        return new TickerInitializer();
//    }
//
//    private Map<ChannelOption<?> , Object> getChannelOptions() {
//        Map<ChannelOption<?>, Object> options = new HashMap<ChannelOption<?>, Object>();
//        options.put(ChannelOption.SO_KEEPALIVE, true);
//        options.put(ChannelOption.SO_BACKLOG, 100);
//        return options;
//    }
//
//    private NioEventLoopGroup getBossGroup(){
//        NioEventLoopGroup bossGroup = new NioEventLoopGroup(2, new NamedThreadFactory("Server-Boss"));
//        return bossGroup;
//    }
//
//    private NioEventLoopGroup getWorkerGroup(){
//        NioEventLoopGroup workerGroup = new NioEventLoopGroup(8, new NamedThreadFactory("Server-Worker"));
//        return workerGroup;
//    }
//}
