package com.github.mouse0w0.instantcoffee.model;

import com.github.mouse0w0.instantcoffee.Location;

import java.util.StringJoiner;

public class Annotation extends Located implements AnnotationValue {
    public static final Annotation[] EMPTY_ARRAY = {};

    public ReferenceType type;
    public AnnotationValuePair[] pairs;
    public boolean visible;

    public Annotation(Location location, ReferenceType type, AnnotationValuePair[] pairs, boolean visible) {
        super(location);
        this.type = type;
        this.pairs = pairs;
        this.visible = visible;
    }

    @Override
    public String toString() {
        if (pairs.length == 0) {
            return "@" + type;
        } else {
            StringJoiner joiner = new StringJoiner(", ", "(", ")");
            for (AnnotationValuePair pair : pairs) {
                joiner.add(pair.toString());
            }
            return "@" + type + joiner;
        }
    }
}
