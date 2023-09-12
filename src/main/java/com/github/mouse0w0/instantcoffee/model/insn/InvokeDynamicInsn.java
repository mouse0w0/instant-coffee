package com.github.mouse0w0.instantcoffee.model.insn;

import com.github.mouse0w0.instantcoffee.Constants;
import com.github.mouse0w0.instantcoffee.Location;
import com.github.mouse0w0.instantcoffee.model.MethodHandle;
import com.github.mouse0w0.instantcoffee.model.Type;

public class InvokeDynamicInsn extends BaseInsn {
    public String name;
    public Type[] parameterTypes;
    public Type returnType;
    public MethodHandle bootstrapMethodHandle;
    public Object[] bootstrapMethodArguments;

    public InvokeDynamicInsn(Location location, String name, Type[] parameterTypes, Type returnType, MethodHandle bootstrapMethodHandle, Object[] bootstrapMethodArguments) {
        super(location, Constants.opcodeToName(Constants.INVOKEDYNAMIC));
        this.name = name;
        this.parameterTypes = parameterTypes;
        this.returnType = returnType;
        this.bootstrapMethodHandle = bootstrapMethodHandle;
        this.bootstrapMethodArguments = bootstrapMethodArguments;
    }
}
