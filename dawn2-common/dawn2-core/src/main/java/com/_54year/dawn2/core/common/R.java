package com._54year.dawn2.core.common;

import com._54year.dawn2.core.constant.CommonMsgEnum;
import lombok.Data;

/**
 * 统一返回结果
 *
 * @param <T> 如果携带data data的类型
 */
@Data
public class R<T> {
    private Integer code;
    private String msg;
    private T data;

    public static R<Object> ok() {
        R<Object> r = new R<>();
        r.setCode(CommonMsgEnum.SUC.getCode());
        r.setMsg(CommonMsgEnum.SUC.getMsg());
        return r;
    }

    public static R<Object> ok(CommonMsgEnum commonMsgEnum) {
        R<Object> r = new R<>();
        r.setCode(commonMsgEnum.getCode());
        r.setMsg(commonMsgEnum.getMsg());
        return r;
    }

    public static <T> R<T> ok(T data, CommonMsgEnum commonMsgEnum) {
        R<T> r = new R<>();
        r.setCode(commonMsgEnum.getCode());
        r.setMsg(commonMsgEnum.getMsg());
        r.setData(data);
        return r;
    }

    public static <T> R<T> ok(T data) {
        R<T> r = new R<>();
        r.setCode(CommonMsgEnum.SUC.getCode());
        r.setMsg(CommonMsgEnum.SUC.getMsg());
        r.setData(data);
        return r;
    }

    public static <T> R<T> ok(T data, String msg) {
        R<T> r = new R<>();
        r.setCode(CommonMsgEnum.SUC.getCode());
        r.setMsg(msg);
        r.setData(data);
        return r;
    }


    public static R<Object> fail() {
        R<Object> r = new R<>();
        r.setCode(CommonMsgEnum.FAIL.getCode());
        r.setMsg(CommonMsgEnum.FAIL.getMsg());
        return r;
    }

    public static R<Object> fail(String msg) {
        R<Object> r = new R<>();
        r.setCode(CommonMsgEnum.FAIL.getCode());
        r.setMsg(msg);
        return r;
    }


    public static R<Object> fail(int code, String msg) {
        R<Object> r = new R<>();
        r.setCode(code);
        r.setMsg(msg);
        return r;
    }

    public static R<Object> fail(CommonMsgEnum commonMsgEnum) {
        R<Object> r = new R<>();
        r.setCode(commonMsgEnum.getCode());
        r.setMsg(commonMsgEnum.getMsg());
        return r;
    }
}
