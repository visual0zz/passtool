package com.zz.passtool.constants.exception;

/**
 * @author 书台
 * @since 2022/4/8 3:26 下午
 */
public class DataFormatTransferException extends RuntimeException {
    public DataFormatTransferException() {
        super();
    }

    public DataFormatTransferException(String message) {
        super(message);
    }

    public DataFormatTransferException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataFormatTransferException(Throwable cause) {
        super(cause);
    }

    protected DataFormatTransferException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
