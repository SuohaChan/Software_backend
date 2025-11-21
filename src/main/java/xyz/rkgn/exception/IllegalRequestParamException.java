package xyz.rkgn.exception;

import xyz.rkgn.exception.BusinessException;

public class IllegalRequestParamException extends BusinessException {
    public IllegalRequestParamException() {
        super("参数错误");
    }

    public IllegalRequestParamException(String message) {
        super(message);
    }

    public IllegalRequestParamException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalRequestParamException(Throwable cause) {
        super(cause);
    }

    public IllegalRequestParamException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
