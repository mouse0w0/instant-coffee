package com.github.mouse0w0.instantcoffee.model;

import com.github.mouse0w0.instantcoffee.Location;

public class VoidType extends Located implements Type {
    public VoidType(Location location) {
        super(location);
    }

    @Override
    public String toString() {
        return "void";
    }
}
