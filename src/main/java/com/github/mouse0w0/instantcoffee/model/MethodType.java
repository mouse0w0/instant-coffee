package com.github.mouse0w0.instantcoffee.model;

import com.github.mouse0w0.instantcoffee.Location;

import java.util.List;
import java.util.StringJoiner;

public class MethodType extends Located implements HandleType {
    public List<Type> parameterTypes;
    public Type returnType;

    public MethodType(Location location, List<Type> parameterTypes, Type returnType) {
        super(location);
        this.parameterTypes = parameterTypes;
        this.returnType = returnType;
    }

    @Override
    public String toString() {
        if (parameterTypes.isEmpty()) {
            return "()" + returnType;
        } else {
            StringJoiner joiner = new StringJoiner(",", "(", ")" + returnType);
            for (Type parameterType : parameterTypes) {
                joiner.add(parameterType.toString());
            }
            return joiner.toString();
        }
    }
}
