package com.haizhi.bqd.service.support;

/**
 * Created by chenbo on 17/4/13.
 */
public enum CustomerIndice implements Indice{
    POC("ddhist");

    String type;

    CustomerIndice(String type) {
        this.type = type;
    }

    public String indice(){
        return this.name().toLowerCase();
    }

    public String type(){
        return this.type;
    }
}
