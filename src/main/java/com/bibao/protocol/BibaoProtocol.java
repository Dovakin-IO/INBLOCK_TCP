package com.bibao.protocol;

/**
 * Created by liuhuanchao on 2018/3/21.
 * 终端与服务的通讯协议
 */
public class BibaoProtocol<T> {

    /**
     * 固定表头
     * byte1 报文类型
     * byte2 报文总长度
     */
    private byte[] fixedHeader;

    private T variableProtocol;
}
