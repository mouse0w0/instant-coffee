package com.github.mouse0w0.instantcoffee.model;

import com.github.mouse0w0.instantcoffee.Location;

public class ClassLiteral extends Located implements Value {
    public Type type;

    public ClassLiteral(Location location, Type type) {
        super(location);
        this.type = type;
    }

    @Override
    public String toString() {
        return type + ".class";
    }
}
