package com.github.mouse0w0.instantcoffee.model.statement;

import com.github.mouse0w0.instantcoffee.Location;
import com.github.mouse0w0.instantcoffee.model.MethodType;
import com.github.mouse0w0.instantcoffee.model.Type;

public class MethodInsn extends BaseInsn {
    public Type owner;
    public String name;
    public MethodType methodType;

    public MethodInsn(Location location, String opcode, Type owner, String name, MethodType methodType) {
        super(location, opcode);
        this.owner = owner;
        this.name = name;
        this.methodType = methodType;
    }
}
