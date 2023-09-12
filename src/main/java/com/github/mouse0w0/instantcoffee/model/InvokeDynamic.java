package com.github.mouse0w0.instantcoffee.model;

import com.github.mouse0w0.instantcoffee.Location;

public class InvokeDynamic extends Located {
    public String name;
    public Type[] parameterTypes;
    public Type returnType;
    public MethodHandle bootstrapMethodHandle;
    public Object[] bootstrapMethodArguments;

    public InvokeDynamic(Location location, String name, Type[] parameterTypes, Type returnType, MethodHandle bootstrapMethodHandle, Object[] bootstrapMethodArguments) {
        super(location);
        this.name = name;
        this.parameterTypes = parameterTypes;
        this.returnType = returnType;
        this.bootstrapMethodHandle = bootstrapMethodHandle;
        this.bootstrapMethodArguments = bootstrapMethodArguments;
    }
}
