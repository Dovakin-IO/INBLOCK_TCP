package com.bibao.redis;

import com.bibao.utils.NetState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

/**
 * Created by liuhuanchao on 2018/3/17.
 */
public class RedisClient {

    static Logger logger = LogManager.getLogger(RedisClient.class.getName());

    public static <T extends Object> T domain(RedisDomainInterface<T> interfaces) throws Exception {
        T object = null;

        Jedis jedis = null;
        jedis = getResource();
//        logger.debug("已连接");
        object = interfaces.domain(jedis);
        RedisPoolClient.releaseResource(jedis);

        return object;
    }

    private static Jedis getResource() throws InterruptedException {
        try{
//            logger.debug("进入jedis获取");
            Jedis jedis = RedisPoolClient.getResource();
            return jedis;
        } catch (Exception e){
            e.printStackTrace();
            NetState netState = new NetState();
            for(;;){
                if (netState.isConnect()) {
                    return getResource();
                }
                logger.debug("正在进行断线重连...");
            }
        }
    }
}
