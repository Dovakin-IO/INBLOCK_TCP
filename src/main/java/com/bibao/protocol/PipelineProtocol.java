package com.bibao.protocol;

/**
 * 管道流传输协议
 * Created by liuhuanchao on 2018/3/19.
 * protocol
 *  byte 0: key长度
 *  byte 1 - byte 4: value长度
 *  Remaining Data: key,value数据
 */
public class PipelineProtocol {

    /**
     * 编码
     * @param key
     * @param value
     * @return
     */
    public static byte[] encode(String key, String value){
        byte[] bkey = key.getBytes();
        byte[] bvalue = value.getBytes();

        /** redis key长度不能超过127字节*/
        byte bkey_length = (byte) key.getBytes().length;
        int ivalue_length = bvalue.length;
        /** redis value长度不能超过65535个字节*/
        byte[] bvalue_length = new byte[] {
                (byte)((ivalue_length >> 24) & 0xFF),
                (byte)((ivalue_length >> 16) & 0xFF),
                (byte)((ivalue_length >> 8) & 0xFF),
                (byte)(ivalue_length  & 0xFF)
        };

        byte[] result = new byte[1 + 4 + bkey.length + bvalue.length];
        result[0] = bkey_length; //第1字节填充key的长度
        System.arraycopy(bvalue_length, 0, result, 1, 4); //第2到5字节填充value长度
        System.arraycopy(bkey,0, result, 5, bkey.length); //填充key的值
        System.arraycopy(bvalue, 0 , result, 5 + bkey_length, bvalue.length); //填充value的值

        return result;
    }

    /**
     * 解码
     * @param data
     * @return String[0] key
     *         String[1] value
     */
    public static String[] decode(byte[] data){
        int keylength = 0 | data[0];
        int valuelength = data[4] & 0xFF |
                (data[3] & 0xFF) << 8 |
                (data[2] & 0xFF) << 16 |
                (data[1] & 0xFF) << 24;
        byte[] bkey = new byte[keylength];
        byte[] bvalue = new byte[valuelength];

        System.arraycopy(data, 5, bkey, 0, keylength);
        System.arraycopy(data, 5 + keylength, bvalue, 0, valuelength);

        return new String[] {
            new String(bkey), new String(bvalue)
        };
    }
}
