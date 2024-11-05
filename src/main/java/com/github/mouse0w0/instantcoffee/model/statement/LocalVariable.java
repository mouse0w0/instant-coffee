package com.github.mouse0w0.instantcoffee.model.statement;

import com.github.mouse0w0.instantcoffee.Location;
import com.github.mouse0w0.instantcoffee.model.IntegerLiteral;
import com.github.mouse0w0.instantcoffee.model.Type;

public class LocalVariable extends Statement {
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
