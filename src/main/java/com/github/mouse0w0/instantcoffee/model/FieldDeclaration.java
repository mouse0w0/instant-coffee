package com.github.mouse0w0.instantcoffee.model;

import com.github.mouse0w0.instantcoffee.Location;

import java.util.ArrayList;
import java.util.List;

public class FieldDeclaration extends Located {
    public List<Annotation> annotations;
    public List<Modifier> modifiers;
    public Type type;
    public String name;
    public Value value;

    public FieldDeclaration(Location location) {
        super(location);
        this.annotations = new ArrayList<>();
        this.modifiers = new ArrayList<>();
    }

    public FieldDeclaration(Location location, List<Annotation> annotations, List<Modifier> modifiers, Type type, String name, Value value) {
        super(location);
        this.annotations = annotations;
        this.modifiers = modifiers;
        this.type = type;
        this.name = name;
        this.value = value;
    }
}
