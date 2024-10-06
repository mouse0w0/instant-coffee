package com.github.mouse0w0.instantcoffee.model.insn;

import com.github.mouse0w0.instantcoffee.Constants;
import com.github.mouse0w0.instantcoffee.Location;
import com.github.mouse0w0.instantcoffee.model.MethodHandle;
import com.github.mouse0w0.instantcoffee.model.MethodType;

public class InvokeDynamicInsn extends BaseInsn {
    public String name;
    public MethodType methodType;
    public MethodHandle bootstrapMethodHandle;
    public Object[] bootstrapMethodArguments;

    public InvokeDynamicInsn(Location location, String name, MethodType methodType, MethodHandle bootstrapMethodHandle, Object[] bootstrapMethodArguments) {
        super(location, Constants.opcodeToName(Constants.INVOKEDYNAMIC));
        this.name = name;
        this.methodType = methodType;
        this.bootstrapMethodHandle = bootstrapMethodHandle;
        this.bootstrapMethodArguments = bootstrapMethodArguments;
    }
}
