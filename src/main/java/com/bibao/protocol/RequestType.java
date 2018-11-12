package com.bibao.protocol;

/**
 * Created by liuhuanchao on 2018/3/23.
 */
public enum  RequestType {
    BINANCE("Ticker:binance", 1),
    BIGONE("Ticket:bigOne", 2),
    GateIO("Ticker:gateIO", 3),
    OKEX("Ticker:OKEX", 4);

    private String name;
    private int index;

    private RequestType(String name, int index){
        this.name = name;
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
