package com.github.mouse0w0.instantcoffee;

import com.github.mouse0w0.instantcoffee.model.MethodDeclaration;
import com.github.mouse0w0.instantcoffee.model.ReferenceType;
import com.github.mouse0w0.instantcoffee.model.Type;

final class MethodContext extends BaseContext {
    private final ClassContext classContext;

    public MethodContext(MethodDeclaration md, ClassContext classContext) {
        super(md.typeParameters);
        this.classContext = classContext;
    }

    @Override
    protected boolean isTypeVariableInParent(ReferenceType type) {
        return classContext.isTypeVariable(type);
    }

    @Override
    protected Type getRawTypeFromParent(Type type) {
        return classContext.getRawType(type);
    }
}