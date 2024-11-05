package com.github.mouse0w0.instantcoffee.model.statement;

import com.github.mouse0w0.instantcoffee.Location;
import com.github.mouse0w0.instantcoffee.model.ReferenceType;

public class TypeInsn extends BaseInsn {
    public ReferenceType type;

    public TypeInsn(Location location, String opcode, ReferenceType type) {
        super(location, opcode);
        this.type = type;
    }
}
