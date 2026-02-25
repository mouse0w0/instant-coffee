package com.github.mouse0w0.instantcoffee;

public class ClassFile {
    private final String className;
    private final String classQualifiedName;
    private final String classInternalName;
    private final byte[] byteArray;

    public ClassFile(String classInternalName, byte[] byteArray) {
        int classNameSeparatorIndex = classInternalName.lastIndexOf('/');
        this.className = classNameSeparatorIndex != -1 ? classInternalName.substring(classNameSeparatorIndex + 1) : classInternalName;
        this.classQualifiedName = classInternalName.replace('/', '.');
        this.classInternalName = classInternalName;
        this.byteArray = byteArray;
    }

    public String getClassName() {
        return className;
    }

    public String getClassQualifiedName() {
        return classQualifiedName;
    }

    public String getClassInternalName() {
        return classInternalName;
    }

    public byte[] toByteArray() {
        return byteArray;
    }
}
