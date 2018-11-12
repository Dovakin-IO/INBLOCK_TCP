package com.bibao.cache;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by liuhuanchao on 2018/3/18.
 */
public class KVCache {

    private final Map<String, String> redisCache;

    public KVCache(ConcurrentHashMap<String,String> redisCache, List<String> keys){
        this.redisCache = redisCache;
        for(String key : keys){
            this.redisCache.put(key,"");
        }
    }

    public String get(String key){
        return redisCache.get(key);
    }

    public void put(String key, String value){
        redisCache.put(key,value);
    }
}
