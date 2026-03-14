package com.github.mouse0w0.instantcoffee;

import com.github.mouse0w0.instantcoffee.model.ClassDeclaration;
import com.github.mouse0w0.instantcoffee.model.Modifier;
import com.github.mouse0w0.instantcoffee.model.ReferenceType;
import com.github.mouse0w0.instantcoffee.model.Type;

final class ClassContext extends BaseContext {
    private final boolean isEnum;

    public ClassContext(ClassDeclaration cd) {
        super(cd.typeParameters);
        isEnum = Modifier.hasModifier(cd.modifiers, "enum");
    }

    public boolean isEnum() {
        return isEnum;
    }

    @Override
    protected boolean isTypeVariableInParent(ReferenceType type) {
        return false;
    }

    @Override
    protected Type getRawTypeFromParent(Type type) {
        return type;
    }
}