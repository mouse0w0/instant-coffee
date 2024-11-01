package com.github.mouse0w0.instantcoffee.model;

import com.github.mouse0w0.instantcoffee.Location;

public class MethodType extends Located implements HandleType {
    public Type[] parameterTypes;
    public Type returnType;

    public MethodType(Location location, Type[] parameterTypes, Type returnType) {
        super(location);
        this.parameterTypes = parameterTypes;
        this.returnType = returnType;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("(");
        if (parameterTypes.length != 0) {
            sb.append(parameterTypes[0]);
            for (int i = 1; i < parameterTypes.length; i++) {
                sb.append(", ").append(parameterTypes[i]);
            }
        }
        return sb.append(")").append(returnType).toString();
    }
}
