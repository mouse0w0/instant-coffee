package com.github.mouse0w0.instantcoffee.model.insn;

import com.github.mouse0w0.instantcoffee.Location;
import com.github.mouse0w0.instantcoffee.model.ReferenceType;
import com.github.mouse0w0.instantcoffee.model.Type;

public class FieldInsn extends BaseInsn {
    public ReferenceType owner;
    public String name;
    public Type type;

    public FieldInsn(Location location, String opcode, ReferenceType owner, String name, Type type) {
        super(location, opcode);
        this.owner = owner;
        this.name = name;
        this.type = type;
    }
}
