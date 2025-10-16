package com.github.mouse0w0.instantcoffee.model;

import com.github.mouse0w0.instantcoffee.Location;

public class InnerClassDeclaration extends Located {
    public static final InnerClassDeclaration[] EMPTY_ARRAY = {};

    public InnerClassType type;
    public Modifier[] modifiers;
    public String[] name;
    public String innerName;

    public InnerClassDeclaration(Location location, InnerClassType type, Modifier[] modifiers, String[] name, String innerName) {
        super(location);
        this.type = type;
        this.modifiers = modifiers;
        this.name = name;
        this.innerName = innerName;
    }
}
