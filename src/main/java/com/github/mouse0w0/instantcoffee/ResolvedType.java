package com.github.mouse0w0.instantcoffee;

public class ResolvedType {
    private final org.objectweb.asm.Type actualType;
    private final String typeArgument;

    public ResolvedType(org.objectweb.asm.Type actualType) {
        this.actualType = actualType;
        this.typeArgument = null;
    }

    public ResolvedType(org.objectweb.asm.Type actualType, String typeArgument) {
        this.actualType = actualType;
        this.typeArgument = typeArgument;
    }

    public org.objectweb.asm.Type getActualType() {
        return actualType;
    }

    public String getInternalName() {
        return actualType.getInternalName();
    }

    public String getDescriptor() {
        return actualType.getDescriptor();
    }

    public boolean isPrimitive() {
        return actualType.getSort() <= org.objectweb.asm.Type.DOUBLE;
    }

    public boolean isArray() {
        return actualType.getSort() == org.objectweb.asm.Type.ARRAY;
    }

    public boolean isObject() {
        return actualType.getSort() == org.objectweb.asm.Type.OBJECT && typeArgument == null;
    }

    public boolean isGeneric() {
        return actualType.getSort() == org.objectweb.asm.Type.OBJECT && typeArgument != null;
    }

    public String getTypeArgument() {
        return typeArgument;
    }
}
