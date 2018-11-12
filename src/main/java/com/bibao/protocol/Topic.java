package com.bibao.protocol;

import com.bibao.server.ExecuteListener;
import com.bibao.server.SubscribeListener;

/**
 * Created by liuhuanchao on 2018/3/18.
 */
public class Topic implements SubscribeListener{
    private String key;
    private ExecuteListener executor;

    public Topic(ExecuteListener executor){
        key = "*";
        this.executor = executor;
    }

    public Topic(ExecuteListener executor, String key){
        this.key = key;
        this.executor = executor;
    }

    public String getKey(){
        return key;
    }

    public void setKey(String key){
        this.key = key;
    }

    @Override
    public void onPublish(String key) {
        if(this.key.equals(key)) executor.onExecute(key);
    }
}
