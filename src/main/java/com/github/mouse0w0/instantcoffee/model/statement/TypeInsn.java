package com.github.mouse0w0.instantcoffee.model.statement;

import com.github.mouse0w0.instantcoffee.Location;
import com.github.mouse0w0.instantcoffee.model.Type;

public class TypeInsn extends BaseInsn {
    public Type type;

    public TypeInsn(Location location, String opcode, Type type) {
        super(location, opcode);
        this.type = type;
    }
}
