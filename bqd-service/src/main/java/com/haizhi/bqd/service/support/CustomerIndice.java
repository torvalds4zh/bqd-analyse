package com.haizhi.bqd.service.support;

/**
 * Created by chenbo on 17/4/13.
 */
public enum CustomerIndice implements Indice{
    POC_DDHIST("poc", "ddhist"),

    POC_PCOA("poc", "pcoa"),

    POC_EBS_S1GL("poc", "EBS_S1GL");

    String indice;

    String type;

    CustomerIndice(String indice, String type) {
        this.indice = indice;
        this.type = type;
    }

    public String indice(){
        return this.indice;
    }

    public String type(){
        return this.type;
    }
}
