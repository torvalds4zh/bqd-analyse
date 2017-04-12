package com.haizhi.bqd.service.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.haizhi.bqd.common.JsonUtil;
import lombok.Data;

/**
 * Created by chenbo on 17/4/6.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Session {
    private Long userId;

    private String username;

    private String password;

    private String salt;

    private String permission;

    private int role;

    private Long webLastLoginTime;

    private Long mobileLastLoginTime;

    private Long deadLineTime;

    private String mobileDeviceId;

    public String toJson() {
        return JsonUtil.formatJSON(this);
    }

    public Session parseJson(String json) {
        return JsonUtil.unformatJSON(json, Session.class);
    }

}

