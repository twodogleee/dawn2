package com._54year.dawn2.core.util;

import com._54year.dawn2.core.exception.DawnParamException;
import org.apache.commons.lang3.ObjectUtils;

/**
 * 异常工具类
 *
 * @author Andersen
 */
public class DawnExceptionUtil {
    /**
     * 如果为空则抛出异常
     *
     * @param obj     数据
     * @param message 错误消息
     */
    public static void isEmpty(Object obj, String message) {
        if (ObjectUtils.isEmpty(obj)) throw new DawnParamException(message);
    }

}
