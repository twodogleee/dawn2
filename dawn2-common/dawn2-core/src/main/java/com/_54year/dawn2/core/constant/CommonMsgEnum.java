package com._54year.dawn2.core.constant;

import lombok.Getter;

/**
 * 基础msg
 *
 * @author Andersen
 */
@Getter
public enum CommonMsgEnum {
    SUC(200, "successful"),
    FAIL(500, "failed");

    CommonMsgEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private final Integer code;
    private final String msg;
}
