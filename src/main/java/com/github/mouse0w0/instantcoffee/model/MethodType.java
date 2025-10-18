package com.github.mouse0w0.instantcoffee.model;

import com.github.mouse0w0.instantcoffee.Location;

import java.util.Iterator;
import java.util.List;

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
            StringBuilder builder = new StringBuilder("(");
            Iterator<Type> it = parameterTypes.iterator();
            builder.append(it.next().toString());
            while (it.hasNext()) {
                builder.append(", ").append(it.next().toString());
            }
            builder.append(")").append(returnType.toString());
            return builder.toString();
        }
    }
}
