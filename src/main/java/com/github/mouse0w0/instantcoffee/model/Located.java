package com.github.mouse0w0.instantcoffee.model;

import com.github.mouse0w0.instantcoffee.Location;

public class Located implements Locatable {
    private final Location location;

    public Located(Location location) {
        this.location = location;
    }

    @Override
    public Location getLocation() {
        return location;
    }
}
