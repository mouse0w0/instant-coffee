package com.github.mouse0w0.instantcoffee.model;

import com.github.mouse0w0.instantcoffee.Location;

public class LocalVariable extends Located {
    public static final LocalVariable[] EMPTY_ARRAY = {};

    public String name;
    public Type type;
    public String start;
    public String end;
    public IntegerLiteral index;

    public LocalVariable(Location location, String name, Type type, String start, String end, IntegerLiteral index) {
        super(location);
        this.name = name;
        this.type = type;
        this.start = start;
        this.end = end;
        this.index = index;
    }
}
