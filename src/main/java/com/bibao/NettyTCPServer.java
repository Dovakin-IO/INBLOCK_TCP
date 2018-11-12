package com.bibao;

import com.bibao.cache.KVCache;
import com.bibao.server.AbstractNettyServer;
import com.bibao.server.NettyConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.Map;
import java.util.Set;

/**
 * Created by liuhuanchao on 2018/3/16.
 */
public class NettyTCPServer extends AbstractNettyServer {

    private ServerBootstrap serverBootstrap;

    public NettyTCPServer(NettyConfig nettyConfig, KVCache kvCache, ChannelInitializer<? extends Channel> channelInitializer) {
        super(nettyConfig, kvCache, channelInitializer);
    }

    public void startServer() throws Exception {
        try {
            serverBootstrap = new ServerBootstrap();
            Map<ChannelOption<?>, Object> channelOptions
                    = nettyConfig.getChannelOptions();
            if(null != channelOptions){
                Set<ChannelOption<?>> keySet = channelOptions.keySet();
                for (ChannelOption option : keySet){
                    serverBootstrap.option(option, channelOptions.get(option));
                }
            }
            serverBootstrap.group(getBossGroup(), getWorkerGroup())
                    .channel(NioServerSocketChannel.class)
                    .childHandler(getChannelInitializer());
            Channel serverChannel = serverBootstrap.bind(nettyConfig.getSocketAddress())
                    .sync().channel();
            ALL_CHANNELS.add(serverChannel);
        } catch (Exception e){
            super.stopServer();
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public TransmissionProtocol getTransmissionProtocol() {
        return TRANSMISSION_PROTOCOL.TCP;
    }

    @Override
    public void setChannelInitializer(ChannelInitializer<? extends Channel> initializer) {
        this.channelInitializer = initializer;
        serverBootstrap.childHandler(initializer);
    }
}
