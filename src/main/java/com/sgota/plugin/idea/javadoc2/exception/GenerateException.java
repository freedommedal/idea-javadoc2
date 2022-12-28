package com.sgota.plugin.idea.javadoc2.exception;

/**
 * @author tiankuo
 */
public class GenerateException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public GenerateException() {
        super();
    }

    public GenerateException(String message) {
        super(message);
    }

    public GenerateException(String message, Throwable cause) {
        super(message, cause);
    }

    public GenerateException(Throwable cause) {
        super(cause);
    }

    protected GenerateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
