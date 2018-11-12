package com.bibao;

import com.bibao.utils.ZLibUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;

/**
 * Created by liuhuanchao on 2018/3/22.
 */
public class TestClient {

    static Logger logger = LogManager.getLogger(TestClient.class.getName());

    private static WebSocketClient client;

    public static void connect() throws URISyntaxException {
        client = new WebSocketClient(new URI("ws://localhost:11750/websocket"), new Draft_6455() {
        }) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                logger.debug("连接建立");
                client.send("Ticker:Binance");
            }

            @Override
            public void onMessage(String s) {
                logger.debug("收到数据");
                byte[] zipData = s.getBytes();
                byte[] data = ZLibUtils.decompress(zipData);
                String text = new String(data);
                logger.debug("实际数据: " + text);
            }

            @Override
            public void onMessage(ByteBuffer bytes) {
                logger.debug("收到数据");
                byte[] zipData = bytes.array();
                byte[] data = ZLibUtils.decompress(zipData);
                String text = new String(data);
                logger.debug("实际数据: " + text);
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                logger.debug("连接关闭");
            }

            @Override
            public void onError(Exception e) {
                logger.debug("错误发生: " + e.getMessage());
            }
        };

        client.connect();
    }

    public static void main(String[] args) {
        try {
            connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
