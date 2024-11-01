package com.github.mouse0w0.instantcoffee.model.insn;

import com.github.mouse0w0.instantcoffee.Constants;
import com.github.mouse0w0.instantcoffee.Location;
import com.github.mouse0w0.instantcoffee.model.Value;

public class LdcInsn extends BaseInsn {
    private static final String OPCODE = Constants.getOpcodeName(Constants.LDC);

    public Value value;

    public LdcInsn(Location location, Value value) {
        super(location, OPCODE);
        this.value = value;
    }
}
