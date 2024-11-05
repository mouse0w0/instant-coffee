package com.github.mouse0w0.instantcoffee.model.statement;

import com.github.mouse0w0.instantcoffee.Location;

public class Label extends Statement {
    public String name;

    public Label(Location location, String name) {
        super(location);
        this.name = name;
    }
}
