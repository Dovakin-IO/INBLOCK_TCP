package com.bibao.server;

import com.bibao.cache.Global;
import com.bibao.protocol.Topic;
import com.bibao.utils.ZLibUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by liuhuanchao on 2018/3/18.
 */
public class Session implements ExecuteListener{

    static Logger logger = LogManager.getLogger(Session.class.getName());

    private Topic topic;
    private Channel channel;

    public Session(Channel channel, String key){
        this.channel = channel;
        topic = new Topic(this, key);

    }

    public void updateTopic(String key){
        byte[] compressed = ZLibUtils.compress(Global.server.getKvCache().get(key).getBytes());
        ByteBuf bytebuf = Unpooled.copiedBuffer(compressed);
        channel.writeAndFlush(new BinaryWebSocketFrame(bytebuf));
        topic.setKey(key);
    }

    @Override
    public void onExecute(String key) {
        logger.debug("推送");
        if(topic.getKey().equals("*") || topic.getKey().equals(key)){
            logger.debug("符合推送条件");
            logger.debug("待推送的数据: " + Global.server.getKvCache().get(key));
            byte[] compressed = ZLibUtils.compress(Global.server.getKvCache().get(key).getBytes());
            ByteBuf bytebuf = Unpooled.copiedBuffer(compressed);
            channel.writeAndFlush(new BinaryWebSocketFrame(bytebuf));

        }
    }
}
