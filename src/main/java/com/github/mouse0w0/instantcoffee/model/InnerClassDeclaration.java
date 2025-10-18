package com.github.mouse0w0.instantcoffee.model;

import com.github.mouse0w0.instantcoffee.Location;

import java.util.List;

public class InnerClassDeclaration extends Located {
    public InnerClassType type;
    public List<Modifier> modifiers;
    public String[] name;
    public String innerName;

    public InnerClassDeclaration(Location location, InnerClassType type, List<Modifier> modifiers, String[] name, String innerName) {
        super(location);
        this.type = type;
        this.modifiers = modifiers;
        this.name = name;
        this.innerName = innerName;
    }
}
