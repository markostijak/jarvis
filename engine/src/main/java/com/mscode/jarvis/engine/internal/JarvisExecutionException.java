package com.mscode.jarvis.engine.internal;

public class JarvisExecutionException extends RuntimeException {

    public JarvisExecutionException() {
        super();
    }

    public JarvisExecutionException(String message) {
        super(message);
    }

    public JarvisExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public JarvisExecutionException(Throwable cause) {
        super(cause);
    }

    protected JarvisExecutionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
