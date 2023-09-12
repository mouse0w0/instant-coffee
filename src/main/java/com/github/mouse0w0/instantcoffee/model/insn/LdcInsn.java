package com.github.mouse0w0.instantcoffee.model.insn;

import com.github.mouse0w0.instantcoffee.Constants;
import com.github.mouse0w0.instantcoffee.Location;
import com.github.mouse0w0.instantcoffee.model.Value;

public class LdcInsn extends BaseInsn {
    public Value value;

    public LdcInsn(Location location, Value value) {
        super(location, Constants.opcodeToName(Constants.LDC));
        this.value = value;
    }
}
