package com.bibao.redis;

import redis.clients.jedis.Jedis;

/**
 * Created by liuhuanchao on 2018/3/17.
 */
public interface RedisDomainInterface<T extends Object> {
    public T domain(Jedis jedis);
}
