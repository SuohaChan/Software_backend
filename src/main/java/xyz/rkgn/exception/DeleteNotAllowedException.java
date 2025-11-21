package xyz.rkgn.exception;

public class DeleteNotAllowedException extends BusinessException {
    public DeleteNotAllowedException() {
    }

    public DeleteNotAllowedException(String message) {
        super(message);
    }

    public DeleteNotAllowedException(String message, Throwable cause) {
        super(message, cause);
    }

    public DeleteNotAllowedException(Throwable cause) {
        super(cause);
    }

    public DeleteNotAllowedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
