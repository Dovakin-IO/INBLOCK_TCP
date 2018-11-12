package com.bibao.factory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by liuhuanchao on 2018/3/16.
 */
public class NamedThreadFactory implements ThreadFactory {

    private static AtomicInteger counter = new AtomicInteger();
    private String name = "Adam";
    private boolean deamon;
    private int priority;

    public NamedThreadFactory(String name){
        this(name, false, -1);
    }

    public NamedThreadFactory(String name, boolean deamon){
        this(name, deamon, -1);
    }

    public NamedThreadFactory(String name, boolean deamon, int priority){
        this.name = name;
        this.deamon = deamon;
        this.priority = priority;
    }


    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r, name + "[" + counter.getAndIncrement() + "]");
        thread.setDaemon(deamon);
        if(priority != -1){
            thread.setPriority(priority);
        }
        return thread;
    }
}
