package com.github.mouse0w0.instantcoffee.model.insn;

import com.github.mouse0w0.instantcoffee.Location;

public class JumpInsn extends BaseInsn {
    public String label;

    public JumpInsn(Location location, String opcode, String label) {
        super(location, opcode);
        this.label = label;
    }
}
