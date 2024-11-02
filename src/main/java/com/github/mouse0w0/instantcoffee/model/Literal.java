package com.github.mouse0w0.instantcoffee.model;

import com.github.mouse0w0.instantcoffee.Location;

public abstract class Literal extends Located implements Value, AnnotationValue {
    public String value;

    public Literal(Location location, String value) {
        super(location);
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
