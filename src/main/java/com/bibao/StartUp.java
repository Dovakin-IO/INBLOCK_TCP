package com.bibao;

import com.bibao.cache.Global;
import com.bibao.redis.RedisPoolClient;
import com.bibao.server.ServerManager;
import com.bibao.utils.AppContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by liuhuanchao on 2018/3/16.
 */
public class StartUp {

    static Logger logger = LogManager.getLogger(StartUp.class.getName());

    public static void main(String[] args) {
        logger.debug("开始解析Spring配置...");
        ApplicationContext applicationContext
                = new ClassPathXmlApplicationContext(new String[]{"classpath:applicationContext.xml"});
        AppContext appContext = new AppContext();
        appContext.setApplicationContext(applicationContext);
        logger.debug("解析完毕...");
        logger.debug("初始化Redis客户端连接池...");
        RedisPoolClient.start();

        ServerManager serverManager = new ServerManager();
        Global.server = serverManager.getServer();

        try {
            serverManager.startServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
