import com.bibao.protocol.PipelineProtocol;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.UnsupportedEncodingException;

/**
 * Created by liuhuanchao on 2018/3/19.
 */
public class StreamTest {

    static Logger logger = LogManager.getLogger(StreamTest.class.getName());

    public void listAllByteValue(){
        for (int i = -128; i < 128; i++) {
            byte b = (byte) i;
            logger.debug("byte:" + b + ",Binary:" + byteToBinaryString(b));
        }
    }

    @Test
    public void pipelineProtocolTest() {
        String key = "Ticker:BigOne";
        String value = "abcdefghijklmnopqrstuvwxyz";

        byte[] data = PipelineProtocol.encode(key, value);

        String[] result = PipelineProtocol.decode(data);

        logger.debug("dkey: " + result[0] + " dvalue: " + result[1]);
    }


    public void testLength() throws UnsupportedEncodingException {
        String a = "Ticker:Binance";
        byte[] aa = a.getBytes();
        logger.debug("length: " + aa.length);
        logger.debug("aa is " + new String(aa));

        byte[] space = " ".getBytes("unicode");
        for(int i = 0; i < space.length; i++){
            logger.debug("space[" + i + "]: " + Integer.toHexString(space[i]));
        }
        int aalength = aa.length;

        byte[] bblength = new byte[] {
                (byte)((aalength >> 24) & 0xFF),
                (byte)((aalength >> 16) & 0xFF),
                (byte)((aalength >> 8) & 0xFF),
                (byte)(aalength  & 0xFF)
        };

        byte[] finalBytes = new byte[bblength.length + aalength];
        System.arraycopy(bblength, 0, finalBytes, 0, bblength.length);
        System.arraycopy(aa, 0, finalBytes, bblength.length, aalength);

        int hlength = finalBytes[3] & 0xFF |
                (finalBytes[2] & 0xFF) << 8 |
                (finalBytes[1] & 0xFF) << 16 |
                (finalBytes[0] & 0xFF) << 24;
        byte[] hdata = new byte[hlength];
        System.arraycopy(finalBytes, 4, hdata, 0, hlength);

        logger.debug("bb is " + new String(hdata));
    }


    public String byteToBinaryString(byte b) {
        String s = Integer.toBinaryString(b);
        if (b < 0) {
            logger.debug("bts:  s: " + s + "sub24(s):  " + s.substring(24));
            s = s.substring(24);
        } else {
            if (s.length() < 8) {
                int len = s.length();
                for (int i = 0; i < 8 - len; i++) {
                    s = "0" + s;
                }
            }
        }
        return s;
    }
}
