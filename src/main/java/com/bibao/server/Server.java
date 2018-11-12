package com.bibao.server;

import java.net.InetSocketAddress;

/**
 * Created by liuhuanchao on 2018/3/16.
 */
public interface Server {

    public interface TransmissionProtocol{

    }

    public enum TRANSMISSION_PROTOCOL implements TransmissionProtocol{
        TCP,
        UDP
    }

    TransmissionProtocol getTransmissionProtocol();

    void startServer() throws Exception;

    void startServer(int port) throws Exception;

    void startServer(InetSocketAddress socketAddress) throws Exception;

    void stopServer() throws Exception;

    InetSocketAddress getInetSocketAddress();
}
