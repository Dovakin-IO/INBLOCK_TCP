package com.bibao.server;

import com.bibao.cache.KVCache;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;

/**
 * Created by liuhuanchao on 2018/3/16.
 */
public abstract class AbstractNettyServer implements NettyServer {

    static Logger logger = LogManager.getLogger(AbstractNettyServer.class.getName());

    // 管理所有channel
    public static final ChannelGroup ALL_CHANNELS
            = new DefaultChannelGroup("NADRON-CHANNELS", GlobalEventExecutor.INSTANCE);

    // 配置
    protected final NettyConfig nettyConfig;

    protected final KVCache kvCache;

    protected ChannelInitializer<? extends Channel> channelInitializer;

    protected AbstractNettyServer(NettyConfig nettyConfig,
                                  KVCache kvCache,
                                  ChannelInitializer<? extends Channel> channelInitializer) {
        this.nettyConfig = nettyConfig;
        this.kvCache = kvCache;
        this.channelInitializer = channelInitializer;
    }

    public TransmissionProtocol getTransmissionProtocol() {
        return null;
    }

    public void startServer(int port) throws Exception {
        nettyConfig.setPortNumber(port);
        nettyConfig.setSocketAddress(new InetSocketAddress(port));

        startServer();
    }

    public void startServer(InetSocketAddress socketAddress) throws Exception {
        nettyConfig.setSocketAddress(socketAddress);

        startServer();
    }

    public void stopServer() throws Exception {

        ChannelGroupFuture future = ALL_CHANNELS.close();
        try {
            future.wait();
        } catch (InterruptedException e){

        } finally {
            if(null != nettyConfig.getBossGroup()){
                nettyConfig.getBossGroup().shutdownGracefully();
            }

            if (null != nettyConfig.getWorkerGroup()){
                nettyConfig.getWorkerGroup().shutdownGracefully();
            }
        }
    }

    public InetSocketAddress getInetSocketAddress() {
        return nettyConfig.getSocketAddress();
    }

    public ChannelInitializer<? extends Channel> getChannelInitializer() {
        return channelInitializer;
    }

    public void setChannelInitializer(ChannelInitializer<? extends Channel> initializer) {

    }

    public NettyConfig getNettyConfig() {
        return nettyConfig;
    }

    public KVCache getKvCache(){ return kvCache; }

    protected EventLoopGroup getBossGroup() {
        return nettyConfig.getBossGroup();
    }

    protected EventLoopGroup getWorkerGroup() {
        return nettyConfig.getWorkerGroup();
    }
}
