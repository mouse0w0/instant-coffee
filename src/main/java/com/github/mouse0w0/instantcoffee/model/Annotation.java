package com.github.mouse0w0.instantcoffee.model;

import com.github.mouse0w0.instantcoffee.Location;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Annotation extends Located implements AnnotationValue {
    public ReferenceType type;
    public List<AnnotationValuePair> pairs;
    public boolean visible;

    public Annotation(Location location) {
        super(location);
        this.pairs = new ArrayList<>();
        this.visible = true;
    }

    public Annotation(Location location, ReferenceType type, List<AnnotationValuePair> pairs, boolean visible) {
        super(location);
        this.type = type;
        this.pairs = pairs;
        this.visible = visible;
    }

    @Override
    public String toString() {
        if (pairs == null || pairs.isEmpty()) {
            return "@" + type;
        } else {
            StringBuilder builder = new StringBuilder("@").append(type.toString()).append("(");
            Iterator<AnnotationValuePair> it = pairs.iterator();
            builder.append(it.next());
            while (it.hasNext()) {
                builder.append(", ").append(it.next());
            }
            return builder.append(")").toString();
        }
    }
}
