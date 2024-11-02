package com.github.mouse0w0.instantcoffee.model;

import com.github.mouse0w0.instantcoffee.Location;

public class NullLiteral extends Located implements Value {
    public NullLiteral(Location location) {
        super(location);
    }

    @Override
    public String toString() {
        return "null";
    }
}
