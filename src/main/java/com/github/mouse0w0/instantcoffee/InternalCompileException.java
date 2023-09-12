package com.github.mouse0w0.instantcoffee;

public class InternalCompileException extends RuntimeException {
    public InternalCompileException() {
    }

    public InternalCompileException(String message) {
        super(message);
    }

    public InternalCompileException(String message, Throwable cause) {
        super(message, cause);
    }
}
