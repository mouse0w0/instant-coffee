package com.github.mouse0w0.instantcoffee.model;

import com.github.mouse0w0.instantcoffee.Location;

public class SourceDeclaration extends Located {
    public StringLiteral file;

    public SourceDeclaration(Location location, StringLiteral file) {
        super(location);
        this.file = file;
    }
}
