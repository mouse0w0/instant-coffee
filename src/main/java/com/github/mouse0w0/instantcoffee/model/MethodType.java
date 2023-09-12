package com.github.mouse0w0.instantcoffee.model;

import com.github.mouse0w0.instantcoffee.Location;

public class MethodType extends Located {
    public Type[] parameterTypes;
    public Type returnType;

    public MethodType(Location location, Type[] parameterTypes, Type returnType) {
        super(location);
    }
}
