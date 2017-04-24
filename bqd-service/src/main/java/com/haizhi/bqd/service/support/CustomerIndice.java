package com.haizhi.bqd.service.support;

/**
 * Created by chenbo on 17/4/13.
 */
public enum CustomerIndice implements Indice{
    POC_DDHIST("poc", "ddhist"),

    POC_PCOA("poc", "pcoa"),

    POC_EBS_S1GL("poc", "EBS_S1GL"),

    POC_SIBS_EBCRDI("poc", "SIBS_EBCRDI"),

    POC_SIBS_DDDHIS("poc", "SIBS_DDDHIS"),

    POC_SIBS_TOTAL("poc", "SIBS_TOTAL"),

    POC_SIBS_GLSLAUTO("poc", "SIBS_GLSLAUTO"),

    POC_SIBS_TLUSTM("poc", "SIBS_TLUSTM"),

    POC_SIBS_GLACCT("poc", "SIBS_GLACCT"),

    POC_SIBS_CFAGRP("poc_test", "SIBS_CFAGRP");

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
