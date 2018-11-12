package com.bibao.server;

/**
 * Created by liuhuanchao on 2018/3/18.
 */
public interface SubscribeListener {

    void onPublish(String key);
}
