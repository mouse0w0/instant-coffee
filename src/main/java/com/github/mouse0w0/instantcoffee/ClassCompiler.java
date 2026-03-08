package com.github.mouse0w0.instantcoffee;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

final class ClassCompiler extends ClassVisitor {
    private final ClassWriter cw;
    private String classInternalName;

    public ClassCompiler() {
        super(Opcodes.ASM9);
        this.cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        this.cv = cw;
    }

    public ClassFile toClassFile() {
        return new ClassFile(classInternalName, cw.toByteArray());
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.classInternalName = name;

        super.visit(version, access, name, signature, superName, interfaces);

        // Fix compute maxs for switch insn
        cw.setFlags(ClassWriter.COMPUTE_MAXS);
    }
}
