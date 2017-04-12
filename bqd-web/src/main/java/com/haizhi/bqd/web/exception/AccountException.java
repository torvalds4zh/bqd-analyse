package com.haizhi.bqd.web.exception;

import com.haizhi.bqd.common.Wrapper;
import com.haizhi.bqd.common.WrapperProvider;

/**
 * Created by chenbo on 17/4/7.
 */
public enum AccountException implements WrapperProvider {
    WRONG_USER_PASS(601, "用户名或密码错误"),

    MISS_ROLE(602, "角色id缺失"),

    MISS_REQUEST_BODY(603, "请求体缺失"),

    MISS_USERNAME(604, "用户名缺失"),

    MISS_PASSWORD(605, "密码参数缺失"),

    WRONG_PASS(606, "原密码错误"),

    ILLEGAL_NEW_PASSWORD(607, "不合法的新密码"),

    USER_NOT_EXISTS(608, "用户不存在"),

    USERNAME_EXISTS(609, "用户名已被占用"),

    USER_GROUP_EXISTS(610, "用户分组名已存在"),

    USER_GROUP_MISS(611, "用户分组名未提供"),

    USER_GROUP_NOT_EXISTS(612, "用户分组不存在"),

    MISS_ID(613, "参数id不能为空或0"),

    WRONG_DELETE_MYSELF(614, "不能删除自己"),

    SYS_GROUP_NAME(615, "\"全部\"为系统分组"),

    OLD_PW_EQ_NEW_PW(616, "新旧密码一致，信息未更改"),

    NEED_ADMIN_ACCESS(617, "需要管理员权限"),

    NAME_LIMIT(618, "组名不能超过16个字符");

    private Integer status;
    private String msg;

    AccountException(Integer status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    @Override
    public Wrapper get() {
        return Wrapper.builder().status(status).msg(msg).build();
    }
}
