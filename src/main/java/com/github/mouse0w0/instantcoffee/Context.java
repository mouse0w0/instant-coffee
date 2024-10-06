package com.github.mouse0w0.instantcoffee;

import com.github.mouse0w0.instantcoffee.model.*;
import org.objectweb.asm.Opcodes;

import java.util.StringJoiner;

public abstract class Context {
    public abstract ResolvedType resolveType(Type type);

    protected static ResolvedType resolveType(Type type, TypeParameter[] typeParameters) {
        if (type instanceof PrimitiveType) {
            return resolvedType2((PrimitiveType) type);
        } else if (type instanceof ArrayType) {
            return resolvedType2((ArrayType) type);
        } else if (type instanceof ReferenceType) {
            return resolvedType2((ReferenceType) type, typeParameters);
        } else {
            throw new InternalCompileException(type.getClass().getName());
        }
    }

    protected static ResolvedType resolvedType2(PrimitiveType type) {
        return new ResolvedType(org.objectweb.asm.Type.getType(getDescriptor2(type)));
    }

    protected static ResolvedType resolvedType2(ArrayType type) {
        return new ResolvedType(org.objectweb.asm.Type.getType(getDescriptor2(type)));
    }

    private static final org.objectweb.asm.Type OBJECT = org.objectweb.asm.Type.getType("Ljava/lang/Object;");

    protected static ResolvedType resolvedType2(ReferenceType type, TypeParameter[] typeParameters) {
        if (type.identifiers.length == 1) {
            String identifier = type.identifiers[0];
            for (TypeParameter typeParameter : typeParameters) {
                if (typeParameter.name.equals(identifier)) {
                    for (ReferenceType bound : typeParameter.bounds) {
                        if (bound != null) {
                            return new ResolvedType(org.objectweb.asm.Type.getType(getDescriptor2(bound)), identifier);
                        }
                    }
                    return new ResolvedType(OBJECT, identifier);
                }
            }
        }
        return new ResolvedType(org.objectweb.asm.Type.getType(getDescriptor2(type)));
    }

    protected static String getDescriptor(Type type) {
        if (type instanceof PrimitiveType) {
            return getDescriptor2((PrimitiveType) type);
        } else if (type instanceof ArrayType) {
            return getDescriptor2((ArrayType) type);
        } else if (type instanceof ReferenceType) {
            return getDescriptor2((ReferenceType) type);
        } else {
            throw new InternalCompileException(type.getClass().getName());
        }
    }

    protected static String getDescriptor2(PrimitiveType type) {
        switch (type.primitive) {
            case VOID:
                return "V";
            case BOOLEAN:
                return "Z";
            case CHAR:
                return "C";
            case BYTE:
                return "B";
            case SHORT:
                return "S";
            case INT:
                return "I";
            case FLOAT:
                return "F";
            case LONG:
                return "J";
            case DOUBLE:
                return "D";
            default:
                throw new InternalCompileException(type.toString());
        }
    }

    protected static String getDescriptor2(ArrayType type) {
        return "[" + getDescriptor(type.componentType);
    }

    protected static String getDescriptor2(ReferenceType type) {
        StringJoiner joiner = new StringJoiner("/", "L", ";");
        for (String identifier : type.identifiers) {
            joiner.add(identifier);
        }
        return joiner.toString();
    }

    public static class ClassContext extends Context {
        private final TypeParameter[] typeParameters;

        public ClassContext(TypeParameter[] typeParameters) {
            this.typeParameters = typeParameters;
        }

        @Override
        public ResolvedType resolveType(Type type) {
            return resolveType(type, typeParameters);
        }
    }

    public static class MethodContext extends Context {
        private final Context parent;
        private final int access;
        private final TypeParameter[] typeParameters;

        public MethodContext(Context parent, int access, TypeParameter[] typeParameters) {
            this.parent = parent;
            this.access = access;
            this.typeParameters = typeParameters;
        }

        private boolean isStatic() {
            return (access & Opcodes.ACC_STATIC) != 0;
        }

        @Override
        public ResolvedType resolveType(Type type) {
            ResolvedType resolvedType = resolveType(type, typeParameters);
            return resolvedType.isObject() && !isStatic() ? parent.resolveType(type) : resolvedType;
        }
    }
}
