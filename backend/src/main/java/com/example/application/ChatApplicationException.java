package com.example.application;

public abstract class ChatApplicationException extends RuntimeException {

    public ChatApplicationException() {
        super();
    }

    public ChatApplicationException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ChatApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChatApplicationException(String message) {
        super(message);
    }

    public ChatApplicationException(Throwable cause) {
        super(cause);
    }
}
