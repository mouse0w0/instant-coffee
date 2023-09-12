package com.github.mouse0w0.instantcoffee.model;

import com.github.mouse0w0.instantcoffee.Location;

public class PrimitiveType extends Located implements Type {
    public Primitive primitive;

    public PrimitiveType(Location location, Primitive primitive) {
        super(location);
        this.primitive = primitive;
    }

    @Override
    public String toString() {
        return primitive.name().toLowerCase();
    }
}
