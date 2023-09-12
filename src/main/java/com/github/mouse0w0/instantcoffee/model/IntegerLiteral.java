package com.github.mouse0w0.instantcoffee.model;

import com.github.mouse0w0.instantcoffee.Location;

public class IntegerLiteral extends Literal {
    public static final IntegerLiteral[] EMPTY_ARRAY = {};

    public IntegerLiteral(Location location, String value) {
        super(location, value);
    }
}
