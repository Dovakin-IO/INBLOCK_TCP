package com.bibao.cache;

import com.bibao.server.AbstractNettyServer;
import com.bibao.server.Session;
import io.netty.channel.ChannelId;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by liuhuanchao on 2018/3/18.
 */
public class Global {

    static Logger logger = LogManager.getLogger(Global.class.getName());

    public static AbstractNettyServer server;

    public static ConcurrentHashMap<ChannelId, Session> session
            = new ConcurrentHashMap<>();

    public static boolean isExist(ChannelId channelId){
        return session.get(channelId) == null ? false : true;
    }

    public static void notifyAll(String key){
        Set sets = session.keySet();
        logger.debug("向客户端发送数据");
        for (Iterator iter = sets.iterator(); iter.hasNext();){
            logger.debug("循环推送");
            Session temp = session.get(iter.next());
            temp.onExecute(key);
        }
    }
}
