package com.haizhi.bqd.service.model;

import com.haizhi.bqd.service.support.DataStatus;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by chenbo on 17/4/6.
 */
@Data
public class UserRole implements Serializable {
    private static final long serialVersionUID = 6143064124241300107L;

    private  Long id;
    private Long roleId;
    private Long userId;
    private int status = DataStatus.NORMAL.ordinal();
    private String comment;
    private long cTime;
    private long uTime;

    public UserRole(){
        this.cTime = System.currentTimeMillis();
        this.uTime = System.currentTimeMillis();
    }

}

