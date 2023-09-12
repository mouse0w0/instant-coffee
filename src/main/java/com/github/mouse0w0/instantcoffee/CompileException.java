package com.github.mouse0w0.instantcoffee;

public class CompileException extends RuntimeException {
    private final Location location;

    public CompileException(String message, Location location) {
        super(message + "\nLocation: " + location);
        this.location = location;
    }

    public CompileException(String message, Location location, Throwable cause) {
        super(message + "\nLocation: " + location, cause);
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }
}
