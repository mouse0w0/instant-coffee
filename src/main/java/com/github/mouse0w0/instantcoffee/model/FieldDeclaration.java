package com.github.mouse0w0.instantcoffee.model;

import com.github.mouse0w0.instantcoffee.Location;

public class FieldDeclaration extends Located {
    public static final FieldDeclaration[] EMPTY_ARRAY = {};

    public Annotation[] annotations;
    public Modifier[] modifiers;
    public Type type;
    public String name;
    public Value value;

    public FieldDeclaration(Location location, Annotation[] annotations, Modifier[] modifiers, Type type, String name, Value value) {
        super(location);
        this.annotations = annotations;
        this.modifiers = modifiers;
        this.type = type;
        this.name = name;
        this.value = value;
    }
}
