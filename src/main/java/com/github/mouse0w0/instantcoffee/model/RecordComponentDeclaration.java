package com.github.mouse0w0.instantcoffee.model;

import com.github.mouse0w0.instantcoffee.Location;

import java.util.ArrayList;
import java.util.List;

public class RecordComponentDeclaration extends Located {
    public List<Annotation> annotations;
    public Type type;
    public String name;

    public RecordComponentDeclaration(Location location) {
        super(location);
        this.annotations = new ArrayList<>();
    }

    public RecordComponentDeclaration(Location location, List<Annotation> annotations, Type type, String name) {
        super(location);
        this.annotations = annotations;
        this.type = type;
        this.name = name;
    }
}
