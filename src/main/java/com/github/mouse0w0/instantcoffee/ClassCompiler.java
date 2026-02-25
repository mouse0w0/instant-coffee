package com.github.mouse0w0.instantcoffee;

import com.github.mouse0w0.instantcoffee.model.ClassDeclaration;
import com.github.mouse0w0.instantcoffee.model.ReferenceType;
import com.github.mouse0w0.instantcoffee.model.TypeParameter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.util.List;

class ClassCompiler extends ClassVisitor implements Scope {
    private final ClassWriter cw;
    private final List<TypeParameter> typeParameters;

    private String classInternalName;

    public ClassCompiler(ClassDeclaration cd) {
        super(Opcodes.ASM9);
        this.cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        this.cv = cw;
        this.typeParameters = cd.typeParameters;
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
