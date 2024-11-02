package com.github.mouse0w0.instantcoffee.model;

import com.github.mouse0w0.instantcoffee.Location;

public class EnumLiteral extends Located implements AnnotationValue {
    public ReferenceType owner;
    public String name;

    public EnumLiteral(Location location, ReferenceType owner, String name) {
        super(location);
        this.owner = owner;
        this.name = name;
    }

    @Override
    public String toString() {
        return owner + "#" + name;
    }
}
