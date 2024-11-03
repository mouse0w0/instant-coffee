package com.github.mouse0w0.instantcoffee.model.insn;

import com.github.mouse0w0.instantcoffee.Constants;
import com.github.mouse0w0.instantcoffee.Location;
import com.github.mouse0w0.instantcoffee.model.PrimitiveType;

public class NewArrayInsn extends BaseInsn {
    private static final String OPCODE = Constants.getOpcodeName(Constants.NEWARRAY);

    public PrimitiveType type;

    public NewArrayInsn(Location location, PrimitiveType type) {
        super(location, OPCODE);
        this.type = type;
    }
}
