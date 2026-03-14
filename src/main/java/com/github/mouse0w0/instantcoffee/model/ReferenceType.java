package com.github.mouse0w0.instantcoffee.model;

import com.github.mouse0w0.instantcoffee.Location;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ReferenceType extends Located implements Type, TypeArgument {
    public List<String> identifiers;
    public List<TypeArgument> typeArguments;

    public ReferenceType(Location location, List<String> identifiers) {
        super(location);
        this.identifiers = identifiers;
        this.typeArguments = new ArrayList<>();
    }

    public ReferenceType(Location location, List<String> identifiers, List<TypeArgument> typeArguments) {
        super(location);
        this.identifiers = identifiers;
        this.typeArguments = typeArguments;
    }

    public static boolean isJavaLangObject(ReferenceType type) {
        return type.identifiers.size() == 3 &&
                "java".equals(type.identifiers.get(0)) &&
                "lang".equals(type.identifiers.get(1)) &&
                "Object".equals(type.identifiers.get(2));
    }

    public static boolean isJavaLangString(ReferenceType type) {
        return type.identifiers.size() == 3 &&
                "java".equals(type.identifiers.get(0)) &&
                "lang".equals(type.identifiers.get(1)) &&
                "String".equals(type.identifiers.get(2));
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        {
            Iterator<String> it = identifiers.iterator();
            builder.append(it.next());
            while (it.hasNext()) {
                builder.append(".").append(it.next());
            }
        }
        if (!typeArguments.isEmpty()) {
            Iterator<TypeArgument> it = typeArguments.iterator();
            builder.append("<").append(it.next());
            while (it.hasNext()) {
                builder.append(", ").append(it.next());
            }
            builder.append(">");
        }
        return builder.toString();
    }
}
