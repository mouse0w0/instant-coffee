package com.github.mouse0w0.instantcoffee.model;

import com.github.mouse0w0.instantcoffee.Location;

public class Modifier extends Located {
    public String keyword;

    public Modifier(Location location, String keyword) {
        super(location);
        this.keyword = keyword;
    }

    @Override
    public String toString() {
        return keyword;
    }
}
