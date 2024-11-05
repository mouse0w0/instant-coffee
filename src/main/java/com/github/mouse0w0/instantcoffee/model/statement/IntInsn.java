package com.github.mouse0w0.instantcoffee.model.statement;

import com.github.mouse0w0.instantcoffee.Location;
import com.github.mouse0w0.instantcoffee.model.IntegerLiteral;

public class IntInsn extends BaseInsn {
    public IntegerLiteral operand;

    public IntInsn(Location location, String opcode, IntegerLiteral operand) {
        super(location, opcode);
        this.operand = operand;
    }
}
