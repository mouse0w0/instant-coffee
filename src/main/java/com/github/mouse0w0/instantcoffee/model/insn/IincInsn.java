package com.github.mouse0w0.instantcoffee.model.insn;

import com.github.mouse0w0.instantcoffee.Constants;
import com.github.mouse0w0.instantcoffee.Location;
import com.github.mouse0w0.instantcoffee.model.IntegerLiteral;

public class IincInsn extends BaseInsn {
    public IntegerLiteral var;
    public IntegerLiteral increment;

    public IincInsn(Location location, IntegerLiteral var, IntegerLiteral increment) {
        super(location, Constants.opcodeToName(Constants.IINC));
        this.var = var;
        this.increment = increment;
    }
}
