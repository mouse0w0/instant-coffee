package com.github.mouse0w0.instantcoffee;

public class CompileException extends RuntimeException {
    private final Location location;

    public CompileException(String message, Location location) {
        super(message);
        this.location = location;
    }

    public CompileException(String message, Location location, Throwable cause) {
        super(message, cause);
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }
}
