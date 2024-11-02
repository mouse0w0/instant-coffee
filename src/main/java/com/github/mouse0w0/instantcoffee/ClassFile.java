package com.github.mouse0w0.instantcoffee;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

public class ClassFile extends ClassVisitor {
    private final ClassWriter cw;

    private String className;
    private String classQualifiedName;
    private String classInternalName;
    private String classDescriptor;

    public ClassFile() {
        super(Opcodes.ASM9);
        cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        cv = cw;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        int classNameSeparatorIndex = name.lastIndexOf('/');
        this.className = classNameSeparatorIndex != -1 ? name.substring(classNameSeparatorIndex + 1) : name;
        this.classQualifiedName = name.replace('/', '.');
        this.classInternalName = name;
        this.classDescriptor = "L" + name + ";";

        super.visit(version, access, name, signature, superName, interfaces);

        // Fix compute maxs for switch insn
        cw.setFlags(ClassWriter.COMPUTE_MAXS);
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

    public String getClassDescriptor() {
        return classDescriptor;
    }

    public byte[] toByteArray() {
        return cw.toByteArray();
    }
}
