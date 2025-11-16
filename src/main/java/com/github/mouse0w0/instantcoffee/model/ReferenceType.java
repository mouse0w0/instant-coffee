package com.github.mouse0w0.instantcoffee.model;

import com.github.mouse0w0.instantcoffee.Location;

import java.util.List;

public class ReferenceType extends Located implements Type {
    public List<String> identifiers;

    public ReferenceType(Location location, List<String> identifier) {
        super(location);
        this.identifiers = identifier;
    }

    @Override
    public String toString() {
        return String.join(".", identifiers);
    }
}
