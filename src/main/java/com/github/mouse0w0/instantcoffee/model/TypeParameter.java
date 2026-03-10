package com.github.mouse0w0.instantcoffee.model;

import com.github.mouse0w0.instantcoffee.Location;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TypeParameter extends Located {
    public String name;
    public List<ReferenceType> bounds;
    public boolean isInterfaceBounds;

    public TypeParameter(Location location) {
        super(location);
        this.bounds = new ArrayList<>();
    }

    public TypeParameter(Location location, String name) {
        super(location);
        this.name = name;
        this.bounds = new ArrayList<>();
    }

    public TypeParameter(Location location, String name, List<ReferenceType> bounds, boolean isInterfaceBounds) {
        super(location);
        this.name = name;
        this.bounds = bounds;
        this.isInterfaceBounds = isInterfaceBounds;
    }

    @Override
    public String toString() {
        if (bounds == null || bounds.isEmpty()) {
            return name;
        }
        StringBuilder builder = new StringBuilder(name).append(isInterfaceBounds ? " implements " : " extends ");
        Iterator<ReferenceType> it = bounds.iterator();
        builder.append(it.next().toString());
        while (it.hasNext()) {
            builder.append(" & ").append(it.next().toString());
        }
        return builder.toString();
    }
}
