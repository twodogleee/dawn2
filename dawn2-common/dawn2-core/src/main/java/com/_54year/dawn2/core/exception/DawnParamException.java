package com._54year.dawn2.core.exception;

/**
 * 参数异常
 *
 * @author Andersen
 */
public class DawnParamException extends RuntimeException {
    public DawnParamException(String message) {
        super(message);
    }

    public DawnParamException(String message, Throwable cause) {
        super(message, cause);
    }

    public DawnParamException(Throwable cause) {
        super(cause);
    }

    public DawnParamException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
