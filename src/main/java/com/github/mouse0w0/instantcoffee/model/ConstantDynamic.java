package com.github.mouse0w0.instantcoffee.model;

import com.github.mouse0w0.instantcoffee.Location;

import java.util.List;

public class ConstantDynamic extends Located implements Value {
    public String name;
    public Type type;
    public Handle bootstrapMethod;
    public List<Value> bootstrapMethodArguments;

    public ConstantDynamic(Location location, String name, Type type, Handle bootstrapMethod, List<Value> bootstrapMethodArguments) {
        super(location);
        this.name = name;
        this.type = type;
        this.bootstrapMethod = bootstrapMethod;
        this.bootstrapMethodArguments = bootstrapMethodArguments;
    }
}
