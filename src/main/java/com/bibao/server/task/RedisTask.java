package com.bibao.server.task;

import com.bibao.cache.Global;
import com.bibao.factory.NamedThreadFactory;
import com.bibao.io.PipedInputStream;
import com.bibao.io.PipedOutputStream;
import com.bibao.protocol.PipelineProtocol;
import com.bibao.redis.RedisClient;
import com.bibao.utils.AppContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * Created by liuhuanchao on 2018/3/19.
 */
public class RedisTask {

    static Logger logger = LogManager.getLogger(RedisTask.class.getName());

    ExecutorService executorService = Executors.newCachedThreadPool();

    protected final int REDIS_DB_INDEX; // Redis数据库index
    protected final int PULL_IDEL; // 轮询间隔
    protected final int PAGE_SIZE; // 管道流缓冲区大小

    /**
     * 管道流读写信号量
     */
    protected static Semaphore readSignal = new Semaphore(0, true);
    protected static Semaphore writeSignal = new Semaphore(1, true);

    /**
     * 读/写 管道流
     */
    protected PipedOutputStream pos;
    protected PipedInputStream pis;

    public RedisTask(int index, int pageSize, int idle) throws IOException {
        REDIS_DB_INDEX = index;
        PULL_IDEL = idle;
        PAGE_SIZE = pageSize;
        pos = new PipedOutputStream();
        pis = new PipedInputStream(pos, PAGE_SIZE);
    }

    public void start(){
        executorService.execute(new NamedThreadFactory("redis-reader").newThread(new RedisReader(pos)));
        executorService.execute(new NamedThreadFactory("socket_writter").newThread(new SocketWritter(pis)));

    }

    private class SocketWritter implements Runnable{

        private PipedInputStream pis;

        public SocketWritter(PipedInputStream pis){
            this.pis = pis;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    readSignal.acquire();

                    ByteArrayOutputStream bos = new ByteArrayOutputStream();

                    byte[] b_key_length = new byte[1];
                    pis.read(b_key_length, 0, 1);
                    int key_length = 0 | b_key_length[0];
                    if(key_length == 0) {
                        writeSignal.release();
                        continue;
                    }

                    byte[] b_value_length = new byte[4];
                    pis.read(b_value_length, 0, 4);
                    int value_length = b_value_length[3] & 0xFF |
                            (b_value_length[2] & 0xFF) << 8 |
                            (b_value_length[1] & 0xFF) << 16 |
                            (b_value_length[0] & 0xFF) << 24;
                    byte[] bytes = new byte[key_length + value_length];

                    int len;
                    if((len = pis.read(bytes)) != -1){
                        bos.write(bytes, 0, len);
                    }
                    writeSignal.release();
                    String[] kv
                            = PipelineProtocol
                            .decode(byteMerger(byteMerger(b_key_length,b_value_length), bos.toByteArray()));
//                    logger.debug("read key: " + kv[0]);
                    bos.close();
                    Global.server.getKvCache().put(kv[0], kv[1]);
                    // 推送实时数据到webSocket客户端
                    Global.notifyAll(kv[0]);
                }

            } catch (Exception e){
                logger.debug("error: " + e.getMessage());
            }
        }
    }

    private class RedisReader implements Runnable {

        private final ArrayList<String> keys;
        private PipedOutputStream pos;

        public RedisReader(PipedOutputStream pos){
            this.pos = pos;
            keys = (ArrayList<String>) AppContext.getBean("cache_keys");
        }

        @Override
        public void run() {
            try {
                while(true){
                    for(String key : keys){
                        writeSignal.acquire();
                        try {
                            RedisClient.domain(jedis -> {
                                jedis.select(REDIS_DB_INDEX);
                                String value = jedis.get(key);
                                if (value == null){
                                    try {
                                        pos.write(new byte[]{0x0});
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    return null;
                                }
                                byte[] data = PipelineProtocol.encode(key, value);
//                                logger.debug("send data size: " + data.length);
                                try {
                                    pos.write(data);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } finally {
    //                                readSignal.release();
                                }
//                                logger.debug("send: key: [" + key + "]");
                                return null;
                            });
                        } catch (Exception e) {
                            try {
                                pos.write(new byte[]{0x0});
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                            e.printStackTrace();
                        } finally {
                            readSignal.release();
                        }
                    }

                    Thread.sleep(PULL_IDEL);
                }
            } catch (InterruptedException e){
                logger.debug("error: " + e.getMessage());
            }
        }
    }

    public static byte[] byteMerger(byte[] bt1, byte[] bt2){
        byte[] bt3 = new byte[bt1.length+bt2.length];
        System.arraycopy(bt1, 0, bt3, 0, bt1.length);
        System.arraycopy(bt2, 0, bt3, bt1.length, bt2.length);
        return bt3;
    }
}
