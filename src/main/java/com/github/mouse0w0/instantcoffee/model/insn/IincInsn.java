package com.github.mouse0w0.instantcoffee.model.insn;

import com.github.mouse0w0.instantcoffee.Constants;
import com.github.mouse0w0.instantcoffee.Location;
import com.github.mouse0w0.instantcoffee.model.IntegerLiteral;

public class IincInsn extends BaseInsn {
    private static final String OPCODE = Constants.getOpcodeName(Constants.IINC);

    public IntegerLiteral var;
    public IntegerLiteral increment;

    public IincInsn(Location location, IntegerLiteral var, IntegerLiteral increment) {
        super(location, OPCODE);
        this.var = var;
        this.increment = increment;
    }
}
