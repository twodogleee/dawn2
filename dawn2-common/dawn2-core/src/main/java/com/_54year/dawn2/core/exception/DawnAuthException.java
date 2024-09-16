package com._54year.dawn2.core.exception;

/**
 * 认证异常
 *
 * @author Andersen
 */
public class DawnAuthException extends RuntimeException {
    public DawnAuthException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public DawnAuthException(Throwable cause) {
        super(cause);
    }

    public DawnAuthException(String message, Throwable cause) {
        super(message, cause);
    }

    public DawnAuthException(String message) {
        super(message);
    }
}
