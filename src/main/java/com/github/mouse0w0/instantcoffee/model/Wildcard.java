package com.github.mouse0w0.instantcoffee.model;

import com.github.mouse0w0.instantcoffee.Location;

public class Wildcard extends Located implements TypeArgument {
    public enum Bounds {
        EXTENDS,
        SUPER
    }

    public Bounds bounds;
    public ReferenceType referenceType;

    public Wildcard(Location location) {
        super(location);
    }

    public Wildcard(Location location, Bounds bounds, ReferenceType referenceType) {
        super(location);
        this.bounds = bounds;
        this.referenceType = referenceType;
    }

    @Override
    public String toString() {
        if (bounds == Bounds.EXTENDS) {
            return "? extends " + referenceType;
        } else if (bounds == Bounds.SUPER) {
            return "? super " + referenceType;
        } else {
            return "?";
        }
    }
}
