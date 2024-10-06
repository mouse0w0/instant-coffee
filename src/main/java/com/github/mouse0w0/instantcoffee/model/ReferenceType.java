package com.github.mouse0w0.instantcoffee.model;

import com.github.mouse0w0.instantcoffee.Location;

public class ReferenceType extends Located implements Type {
    public static final ReferenceType[] EMPTY_ARRAY = {};

    public String[] identifiers;

    public ReferenceType(Location location, String[] identifier) {
        super(location);
        this.identifiers = identifier;
    }

    @Override
    public String toString() {
        return String.join(".", identifiers);
    }
}
