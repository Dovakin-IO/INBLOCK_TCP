package com.bibao.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Created by liuhuanchao on 2018/3/16.
 */
public class AppContext implements ApplicationContextAware {

    public static final String TCP_SERVER = "tcpServer";

    private static ApplicationContext applicationContext;

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        AppContext.applicationContext = applicationContext;
    }

    public static Object getBean(String beanName){
        if(null == beanName){
            return null;
        }
        return applicationContext.getBean(beanName);
    }
}
