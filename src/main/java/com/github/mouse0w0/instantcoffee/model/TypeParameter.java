package com.github.mouse0w0.instantcoffee.model;

import com.github.mouse0w0.instantcoffee.Location;

public class TypeParameter extends Located {
    public static final TypeParameter[] EMPTY_ARRAY = {};

    public String name;
    public ReferenceType[] bounds;

    public TypeParameter(Location location, String name) {
        super(location);
        this.name = name;
        this.bounds = ReferenceType.EMPTY_ARRAY;
    }

    public TypeParameter(Location location, String name, ReferenceType[] bounds) {
        super(location);
        this.name = name;
        this.bounds = bounds;
    }
}
