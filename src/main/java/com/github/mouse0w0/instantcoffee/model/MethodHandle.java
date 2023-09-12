package com.github.mouse0w0.instantcoffee.model;

import com.github.mouse0w0.instantcoffee.Location;

public class MethodHandle extends Located {
    public String tag;
    public Type owner;
    public String name;
    public Type[] parameterTypes;
    public Type returnType;

    public MethodHandle(Location location, String tag, Type owner, String name, Type[] parameterTypes, Type returnType) {
        super(location);
        this.tag = tag;
        this.owner = owner;
        this.name = name;
        this.parameterTypes = parameterTypes;
        this.returnType = returnType;
    }
}
