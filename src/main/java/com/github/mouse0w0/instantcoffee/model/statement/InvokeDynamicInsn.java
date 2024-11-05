package com.github.mouse0w0.instantcoffee.model.statement;

import com.github.mouse0w0.instantcoffee.Constants;
import com.github.mouse0w0.instantcoffee.Location;
import com.github.mouse0w0.instantcoffee.model.Handle;
import com.github.mouse0w0.instantcoffee.model.MethodType;
import com.github.mouse0w0.instantcoffee.model.Value;

public class InvokeDynamicInsn extends BaseInsn {
    private static final String OPCODE = Constants.getOpcodeName(Constants.INVOKEDYNAMIC);

    public String name;
    public MethodType methodType;
    public Handle bootstrapMethod;
    public Value[] bootstrapMethodArguments;

    public InvokeDynamicInsn(Location location, String name, MethodType methodType, Handle bootstrapMethod, Value[] bootstrapMethodArguments) {
        super(location, OPCODE);
        this.name = name;
        this.methodType = methodType;
        this.bootstrapMethod = bootstrapMethod;
        this.bootstrapMethodArguments = bootstrapMethodArguments;
    }
}
