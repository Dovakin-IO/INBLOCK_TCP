package com.bibao.server;

import com.bibao.server.task.RedisTask;
import com.bibao.utils.AppContext;

import java.io.IOException;

/**
 * Created by liuhuanchao on 2018/3/16.
 */
public class ServerManager {
    private AbstractNettyServer tcpServer;

    public ServerManager() {
        tcpServer = (AbstractNettyServer) AppContext.getBean(AppContext.TCP_SERVER);
    }

//    public ServerManager() {
//        tcpServer = new StartupInitializer().getServer();
//    }

    public void startServer(int port) throws Exception{
        tcpServer.startServer(port);
        startTask();
    }

    public void startServer() throws Exception{
        tcpServer.startServer();
        startTask();
    }

    public void stopServer() throws Exception{
        tcpServer.stopServer();
    }

    private void startTask() throws IOException {
        RedisTask redisTask = (RedisTask) AppContext.getBean("redisTask");
        redisTask.start();
    }

    public AbstractNettyServer getServer(){
        return this.tcpServer;
    }
}
