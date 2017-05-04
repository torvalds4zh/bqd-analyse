package com.haizhi.bqd.web.exception;

import com.haizhi.bqd.common.Wrapper;
import com.haizhi.bqd.common.WrapperProvider;

/**
 * Created by chenbo on 17/4/16.
 */
public enum SearchException  implements WrapperProvider {
    MISS_CARD(701, "卡号不能为空"),

    MISS_ACCTNO(702, "账号不能为空"),

    MISS_TR_BEGIN_DATE(703, "交易起始日期不能为空"),

    MISS_TR_END_DATE(702, "交易结束日期不能为空");

    private Integer status;
    private String msg;

    SearchException(Integer status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    @Override
    public Wrapper get() {
        return Wrapper.builder().status(status).msg(msg).build();
    }
}
