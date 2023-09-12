package com.github.mouse0w0.instantcoffee.model.insn;

import com.github.mouse0w0.instantcoffee.Location;
import com.github.mouse0w0.instantcoffee.model.IntegerLiteral;

public class VarInsn extends BaseInsn {
    public IntegerLiteral var;

    public VarInsn(Location location, String opcode, IntegerLiteral var) {
        super(location, opcode);
        this.var = var;
    }
}
