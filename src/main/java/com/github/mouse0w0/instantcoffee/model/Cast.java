package com.github.mouse0w0.instantcoffee.model;

import com.github.mouse0w0.instantcoffee.Location;

public class Cast extends Located implements Value {
    public Type type;
    public Value value;

    public Cast(Location location, Type type, Value value) {
        super(location);
        this.type = type;
        this.value = value;
    }

    @Override
    public String toString() {
        return "(" + type + ") " + value;
    }
}
