package com.github.mouse0w0.instantcoffee.model;

import com.github.mouse0w0.instantcoffee.Location;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ReferenceType extends Located implements Type, TypeArgument {
    public List<String> identifiers;
    public List<TypeArgument> typeArguments;

    public ReferenceType(Location location, List<String> identifier) {
        super(location);
        this.identifiers = identifier;
        this.typeArguments = new ArrayList<>();
    }

    public ReferenceType(Location location, List<String> identifier, List<TypeArgument> typeArguments) {
        super(location);
        this.identifiers = identifier;
        this.typeArguments = typeArguments;
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
