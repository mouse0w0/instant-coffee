package com.github.mouse0w0.instantcoffee.model.insn;

import com.github.mouse0w0.instantcoffee.Location;
import com.github.mouse0w0.instantcoffee.model.Type;

public class MethodInsn extends BaseInsn {
    public Type owner;
    public String name;
    public Type[] parameterTypes;
    public Type returnType;

    public MethodInsn(Location location, String opcode, Type owner, String name, Type[] parameterTypes, Type returnType) {
        super(location, opcode);
        this.owner = owner;
        this.name = name;
        this.parameterTypes = parameterTypes;
        this.returnType = returnType;
    }
}
