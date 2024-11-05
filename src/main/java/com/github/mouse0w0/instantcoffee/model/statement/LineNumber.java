package com.github.mouse0w0.instantcoffee.model.statement;

import com.github.mouse0w0.instantcoffee.Location;
import com.github.mouse0w0.instantcoffee.model.IntegerLiteral;

public class LineNumber extends Statement {
    public IntegerLiteral line;
    public String label;

    public LineNumber(Location location, IntegerLiteral line, String label) {
        super(location);
        this.line = line;
        this.label = label;
    }
}
