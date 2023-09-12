package com.github.mouse0w0.instantcoffee.model;

import com.github.mouse0w0.instantcoffee.Location;

import java.util.StringJoiner;

public class ReferenceType extends Located implements Type, TypeArgument {
    public static final ReferenceType[] EMPTY_ARRAY = {};

    public String[] identifiers;
    public TypeArgument[] typeArguments;

    public ReferenceType(Location location, String[] identifier) {
        super(location);
        this.identifiers = identifier;
        this.typeArguments = TypeArgument.EMPTY_ARRAY;
    }

    public ReferenceType(Location location, String[] identifier, TypeArgument[] typeArguments) {
        super(location);
        this.identifiers = identifier;
        this.typeArguments = typeArguments;
    }

    @Override
    public String toString() {
        if (typeArguments.length == 0) {
            return String.join(".", identifiers);
        } else {
            StringJoiner joiner = new StringJoiner(", ", "<", ">");
            for (TypeArgument typeArgument : typeArguments) {
                joiner.add(typeArgument.toString());
            }
            return String.join(".", identifiers) + joiner;
        }
    }
}
