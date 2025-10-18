package com.github.mouse0w0.instantcoffee.model;

import com.github.mouse0w0.instantcoffee.Location;

public class AnnotationValuePair extends Located {
    public String key;
    public AnnotationValue value;

    public AnnotationValuePair(Location location, String key, AnnotationValue value) {
        super(location);
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return key + "=" + value;
    }
}
