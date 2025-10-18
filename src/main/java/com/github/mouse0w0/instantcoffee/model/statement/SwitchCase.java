package com.github.mouse0w0.instantcoffee.model.statement;

import com.github.mouse0w0.instantcoffee.Location;
import com.github.mouse0w0.instantcoffee.model.IntegerLiteral;
import com.github.mouse0w0.instantcoffee.model.Located;

public class SwitchCase extends Located {
    public IntegerLiteral key;
    public String label;

    public SwitchCase(Location location, IntegerLiteral key, String label) {
        super(location);
        this.key = key;
        this.label = label;
    }
}
