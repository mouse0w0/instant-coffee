package com.github.mouse0w0.instantcoffee.model.statement;

import com.github.mouse0w0.instantcoffee.Location;

public abstract class BaseInsn extends Statement {
    public String opcode;

    public BaseInsn(Location location, String opcode) {
        super(location);
        this.opcode = opcode;
    }
}
