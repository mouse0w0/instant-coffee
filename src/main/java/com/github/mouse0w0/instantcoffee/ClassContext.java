package com.github.mouse0w0.instantcoffee;

import com.github.mouse0w0.instantcoffee.model.ClassDeclaration;
import com.github.mouse0w0.instantcoffee.model.ReferenceType;
import com.github.mouse0w0.instantcoffee.model.Type;

final class ClassContext extends BaseContext {
    public ClassContext(ClassDeclaration cd) {
        super(cd.typeParameters);
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