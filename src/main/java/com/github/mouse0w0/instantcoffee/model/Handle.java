package com.github.mouse0w0.instantcoffee.model;

import com.github.mouse0w0.instantcoffee.Location;

public class Handle extends Located implements Value {
    public String kind;
    public ReferenceType owner;
    public String name;
    public HandleType type;

    public Handle(Location location, String kind, ReferenceType owner, String name, HandleType type) {
        super(location);
        this.kind = kind;
        this.owner = owner;
        this.name = name;
        this.type = type;
    }
}
