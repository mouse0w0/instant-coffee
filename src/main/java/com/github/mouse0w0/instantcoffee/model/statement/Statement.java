package com.github.mouse0w0.instantcoffee.model.statement;

import com.github.mouse0w0.instantcoffee.Location;
import com.github.mouse0w0.instantcoffee.model.Located;

public abstract class Statement extends Located {
    public Statement(Location location) {
        super(location);
    }
}
