package com.github.mouse0w0.instantcoffee;

import com.github.mouse0w0.instantcoffee.model.ClassDeclaration;
import com.github.mouse0w0.instantcoffee.model.ReferenceType;
import com.github.mouse0w0.instantcoffee.model.TypeParameter;

import java.util.List;

final class ClassContext implements Context {
    private final List<TypeParameter> typeParameters;

    public ClassContext(ClassDeclaration cd) {
        this.typeParameters = cd.typeParameters;
    }

    @Override
    public boolean isTypeVariable(ReferenceType type) {
        if (type.identifiers.size() != 1) return false;
        if (!type.typeArguments.isEmpty()) return false;
        String typeVariableName = type.identifiers.get(0);
        for (TypeParameter typeParameter : typeParameters) {
            if (typeParameter.name.equals(typeVariableName)) {
                return true;
            }
        }
        return false;
    }
}
