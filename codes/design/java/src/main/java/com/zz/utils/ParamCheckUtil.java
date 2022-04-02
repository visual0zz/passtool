package com.zz.utils;

/**
 * @author 书台
 * @since 2022/4/11 4:11 下午
 */
public class ParamCheckUtil {
    public static void assertTrue(boolean condition, String errorInfo) {
        if (!condition) {
            throw new ParamCheckException(errorInfo);
        }
    }

    public static void assertTrue(boolean condition, RuntimeException e) {
        if (!condition) {
            throw e;
        }
    }

    public static class ParamCheckException extends RuntimeException {
        public ParamCheckException(String message) {
            super(message);
        }
    }
}
