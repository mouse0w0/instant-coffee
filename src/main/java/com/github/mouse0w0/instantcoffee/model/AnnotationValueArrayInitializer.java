package com.github.mouse0w0.instantcoffee.model;

import com.github.mouse0w0.instantcoffee.Location;

import java.util.StringJoiner;

public class AnnotationValueArrayInitializer extends Located implements AnnotationValue {
    public AnnotationValue[] values;

    public AnnotationValueArrayInitializer(Location location, AnnotationValue[] values) {
        super(location);
        this.values = values;
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(", ", "{", "}");
        for (AnnotationValue value : values) {
            joiner.add(value.toString());
        }
        return joiner.toString();
    }
}
