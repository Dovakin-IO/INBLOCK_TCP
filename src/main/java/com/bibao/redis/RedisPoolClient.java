package com.bibao.redis;

import com.bibao.utils.AppContext;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Created by liuhuanchao on 2018/3/17.
 */
public class RedisPoolClient {

    private static final String POOL_ID = "jedisPool";

    private static JedisPool pool;

    public static void start(){
        pool = (JedisPool) AppContext.getBean(POOL_ID);
    }

    public static Jedis getResource(){
        return pool.getResource();
    }

    public static void releaseResource(Jedis jedis) {
        if(null != jedis) jedis.close();
    }
}
