package com.github.mouse0w0.instantcoffee.model;

import com.github.mouse0w0.instantcoffee.Location;

public class InnerClassDeclaration extends Located {
    public static final InnerClassDeclaration[] EMPTY_ARRAY = {};

    public Modifier[] modifiers;
    public String[] identifiers;

    public InnerClassDeclaration(Location location, Modifier[] modifiers, String[] identifiers) {
        super(location);
        this.modifiers = modifiers;
        this.identifiers = identifiers;
    }
}
