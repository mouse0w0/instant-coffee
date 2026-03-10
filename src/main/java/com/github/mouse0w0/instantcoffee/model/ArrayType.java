package com.github.mouse0w0.instantcoffee.model;

import com.github.mouse0w0.instantcoffee.Location;

public class ArrayType extends Located implements Type, TypeArgument {
    public Type componentType;

    public ArrayType(Location location, Type componentType) {
        super(location);
        this.componentType = componentType;
    }

    @Override
    public String toString() {
        return componentType + "[]";
    }
}
