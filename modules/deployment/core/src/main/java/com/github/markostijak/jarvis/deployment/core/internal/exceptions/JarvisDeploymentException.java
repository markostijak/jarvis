package com.github.markostijak.jarvis.deployment.core.internal.exceptions;

import com.github.markostijak.jarvis.engine.internal.exceptions.JarvisException;

public class JarvisDeploymentException extends JarvisException {

    public JarvisDeploymentException(String message) {
        super(message);
    }

    public JarvisDeploymentException(String message, Throwable cause) {
        super(message, cause);
    }

}
