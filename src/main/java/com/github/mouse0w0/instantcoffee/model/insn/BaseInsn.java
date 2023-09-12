package com.github.mouse0w0.instantcoffee.model.insn;

import com.github.mouse0w0.instantcoffee.Location;
import com.github.mouse0w0.instantcoffee.model.Located;

public abstract class BaseInsn extends Located {
    public static final BaseInsn[] EMPTY_ARRAY = {};

    public String opcode;

    public BaseInsn(Location location, String opcode) {
        super(location);
        this.opcode = opcode;
    }
}
