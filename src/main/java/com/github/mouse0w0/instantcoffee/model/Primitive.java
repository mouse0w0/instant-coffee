package com.github.mouse0w0.instantcoffee.model;

public enum Primitive {
    VOID,
    BOOLEAN,
    CHAR,
    BYTE,
    SHORT,
    INT,
    FLOAT,
    LONG,
    DOUBLE;

    private final String lowerCaseName;

    Primitive() {
        this.lowerCaseName = name().toLowerCase();
    }

    public String lowerCaseName() {
        return lowerCaseName;
    }
}
