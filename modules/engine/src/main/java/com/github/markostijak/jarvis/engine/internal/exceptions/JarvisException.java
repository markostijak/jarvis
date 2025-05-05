package com.github.markostijak.jarvis.engine.internal.exceptions;

public class JarvisException extends RuntimeException {

    public JarvisException(String message) {
        super(message);
    }

    public JarvisException(String message, Throwable cause) {
        super(message, cause);
    }

}
