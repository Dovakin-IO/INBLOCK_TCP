import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by liuhuanchao on 2018/3/17.
 */
public class WebSocketTest {

    static Logger logger = LogManager.getLogger(WebSocketTest.class.getName());
    private WebSocketClient client;

    @Test
    public void connect() throws URISyntaxException {
        client = new WebSocketClient(new URI("ws://localhost:11750/websocket"), new Draft_6455() {
        }) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                logger.debug("连接建立");
            }

            @Override
            public void onMessage(String s) {
                logger.debug("收到数据: " + s);
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
}
