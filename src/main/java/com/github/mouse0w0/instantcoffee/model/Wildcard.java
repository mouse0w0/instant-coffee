package com.github.mouse0w0.instantcoffee.model;

import com.github.mouse0w0.instantcoffee.Location;

public class Wildcard extends Located implements TypeArgument {
    public static final int BOUNDS_NONE = 0;
    public static final int BOUNDS_EXTENDS = 1;
    public static final int BOUNDS_SUPER = 2;

    public int bounds;
    public ReferenceType type;

    public Wildcard(Location location) {
        this(location, BOUNDS_NONE, null);
    }

    public Wildcard(Location location, int bounds, ReferenceType type) {
        super(location);
        if (bounds != BOUNDS_NONE && bounds != BOUNDS_EXTENDS && bounds != BOUNDS_SUPER) {
            throw new IllegalArgumentException("bounds");
        }
        this.bounds = bounds;
        this.type = type;
    }

    @Override
    public String toString() {
        switch (bounds) {
            case BOUNDS_NONE:
                return "?";
            case BOUNDS_EXTENDS:
                return "? extends " + type;
            case BOUNDS_SUPER:
                return "? super " + type;
            default:
                throw new Error("unreachable");
        }
    }
}
