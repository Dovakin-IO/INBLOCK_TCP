import com.bibao.redis.RedisClient;
import com.bibao.redis.RedisPoolClient;
import com.bibao.utils.AppContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import redis.clients.jedis.Jedis;

/**
 * Created by liuhuanchao on 2018/3/17.
 */
public class RedisTest {

    static Logger logger = LogManager.getLogger(RedisTest.class.getName());

//    @Test
//    public void connect(){
//        Jedis jedis = new Jedis("lianghua.haowankeji.cn", 56379);
//        jedis.auth("kyuubi#bibao");
//        jedis.select(1);
//        logger.debug("redis: " + jedis.get("Ticker:Binance"));
//        jedis.close();
//    }
//
//    @Test
//    public void clientTest(){
//
//        logger.debug("开始解析Spring配置...");
//        ApplicationContext applicationContext
//                = new ClassPathXmlApplicationContext(new String[]{"classpath:applicationContext.xml"});
//        AppContext appContext = new AppContext();
//        appContext.setApplicationContext(applicationContext);
//        logger.debug("解析完毕...");
//        logger.debug("初始化Redis客户端连接池...");
//        RedisPoolClient.start();
//        final String[] resource = {""};
//        long start = System.currentTimeMillis();
//        logger.debug("" + RedisClient.domain(jedis -> {
//            jedis.select(1);
//            resource[0] = jedis.get("Ticker:Binance");
//            return resource[0];
//        }));
//        long end = System.currentTimeMillis();
//        logger.debug("resource size: " + resource[0].getBytes().length + "byte");
//        logger.debug("with " + (start - end) + " millis");
//    }

    @Test
    public void pullTest() throws InterruptedException {
        logger.debug("开始解析Spring配置...");
        ApplicationContext applicationContext
                = new ClassPathXmlApplicationContext(new String[]{"classpath:applicationContext.xml"});
        AppContext appContext = new AppContext();
        appContext.setApplicationContext(applicationContext);
        logger.debug("解析完毕...");
        logger.debug("初始化Redis客户端连接池...");
        RedisPoolClient.start();
        int i = 10;
        while(i > 0){
            final String[] resource = {""};
            long start = System.currentTimeMillis();
            try {
                logger.debug("" + RedisClient.domain(jedis -> {
                    jedis.select(1);
                    resource[0] = jedis.get("Ticker:Binance");
                    return resource[0];
                }));
            } catch (Exception e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            logger.debug("resource size: " + resource[0].getBytes().length + "byte");
            logger.debug("with " + (start - end) + " millis");

            Thread.sleep(2000);
            i--;
        }
    }


}
