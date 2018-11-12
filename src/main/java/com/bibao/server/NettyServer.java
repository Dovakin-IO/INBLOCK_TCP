package com.bibao.server;


import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

/**
 * Created by liuhuanchao on 2018/3/16.
 */
public interface NettyServer extends Server{

    public ChannelInitializer<? extends Channel> getChannelInitializer();

    public void setChannelInitializer(ChannelInitializer<? extends Channel> initializer);

    public NettyConfig getNettyConfig();
}
