package com.github.mouse0w0.instantcoffee.model.statement;

import com.github.mouse0w0.instantcoffee.Location;
import com.github.mouse0w0.instantcoffee.model.ReferenceType;

public class TryCatchBlock extends Statement {
    public String start;
    public String end;
    public String handler;
    public ReferenceType type;

    public TryCatchBlock(Location location, String start, String end, String handler, ReferenceType type) {
        super(location);
        this.start = start;
        this.end = end;
        this.handler = handler;
        this.type = type;
    }
}
