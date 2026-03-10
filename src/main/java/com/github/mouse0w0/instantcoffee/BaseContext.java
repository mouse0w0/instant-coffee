package com.github.mouse0w0.instantcoffee;

import com.github.mouse0w0.instantcoffee.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

abstract class BaseContext implements Context {
    protected final List<TypeParameter> typeParameters;

    protected BaseContext(List<TypeParameter> typeParameters) {
        this.typeParameters = typeParameters;
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
        return isTypeVariableInParent(type);
    }

    protected abstract boolean isTypeVariableInParent(ReferenceType type);

    @Override
    public Type getRawType(Type type) {
        if (type instanceof PrimitiveType) {
            return type;
        } else if (type instanceof ArrayType) {
            return new ArrayType(Location.UNKNOWN, getRawType(((ArrayType) type).componentType));
        } else if (type instanceof ReferenceType) {
            return getRawType2((ReferenceType) type);
        } else {
            throw new InternalCompileException(type.getClass().getName());
        }
    }

    private static final ReferenceType OBJECT_TYPE = new ReferenceType(Location.UNKNOWN, new ArrayList<>(Arrays.asList("java", "lang", "Object")));

    private Type getRawType2(ReferenceType type) {
        if (type.identifiers.size() != 1) return type;
        if (!type.typeArguments.isEmpty()) return type;
        String typeVariable = type.identifiers.get(0);
        for (TypeParameter typeParameter : typeParameters) {
            if (typeParameter.name.equals(typeVariable)) {
                return typeParameter.bounds.isEmpty() ? OBJECT_TYPE : getRawType2(typeParameter.bounds.get(0));
            }
        }
        return getRawTypeFromParent(type);
    }

    protected abstract Type getRawTypeFromParent(Type type);
}
