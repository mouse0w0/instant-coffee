package com.github.mouse0w0.instantcoffee.model;

import com.github.mouse0w0.instantcoffee.Location;

public class AmbiguousName extends Located implements Value {
    public String[] identifiers;

    public AmbiguousName(Location location, String... identifiers) {
        super(location);
        this.identifiers = identifiers;
    }

    @Override
    public String toString() {
        return String.join(".", identifiers);
    }
}
