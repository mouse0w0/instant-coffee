package com.github.mouse0w0.instantcoffee;

import com.github.mouse0w0.instantcoffee.model.*;
import com.github.mouse0w0.instantcoffee.model.statement.*;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.*;

import static com.github.mouse0w0.instantcoffee.Constants.*;

public class Compiler {
    public Compiler() {
    }

    public ClassFile compile(ClassDeclaration cd) {
        return compile(cd, new ClassFile());
    }

    public ClassFile compile(ClassDeclaration cd, ClassFile cf) {
        int version = getVersion(cd.version);
        int access = getClassAccess(cd.modifiers);
        String name = getInternalName2(cd.identifiers);
        String superclass = getSuperclass(cd.superclass);
        String[] interfaces = getInterfaces(cd.interfaces);
        cf.visit(version, access, name, null, superclass, interfaces); // TODO: signature
        compileSource(cd.source, cf);
        compileAnnotations(cd.annotations, cf);
        compileInnerClasses(cd.innerClasses, cf);
        compileFields(cd.fields, cf);
        compileMethods(cd.methods, cf);
        cf.visitEnd();
        return cf;
    }

    private String getInternalName(Type type) {
        if (type instanceof ReferenceType) {
            return getInternalName2((ReferenceType) type);
        } else {
            return getDescriptor(type);
        }
    }

    private String getInternalName2(ReferenceType type) {
        return getInternalName2(type.identifiers);
    }

    private String getInternalName2(String[] identifiers) {
        if (identifiers.length == 1) {
            return identifiers[0];
        }
        StringBuilder stringBuilder = new StringBuilder(identifiers[0]);
        for (int i = 1; i < identifiers.length; i++) {
            stringBuilder.append("/").append(identifiers[i]);
        }
        return stringBuilder.toString();
    }

    private String getDescriptor(Type type) {
        if (type instanceof PrimitiveType) {
            return getDescriptor2((PrimitiveType) type);
        } else if (type instanceof ArrayType) {
            return getDescriptor2((ArrayType) type);
        } else if (type instanceof ReferenceType) {
            return getDescriptor2((ReferenceType) type);
        } else if (type instanceof VoidType) {
            return getDescriptor2((VoidType) type);
        } else {
            throw new InternalCompileException(type.getClass().getName());
        }
    }

    private String getDescriptor(String[] identifiers) {
        return "L" + getInternalName2(identifiers) + ";";
    }

    private String getDescriptor2(PrimitiveType type) {
        switch (type.primitive) {
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

    private String getDescriptor2(ArrayType type) {
        return "[" + getDescriptor(type.componentType);
    }

    private String getDescriptor2(ReferenceType type) {
        return "L" + getInternalName2(type.identifiers) + ";";
    }

    private String getDescriptor2(VoidType type) {
        return "V";
    }

    private String getMethodDescriptor(MethodType type) {
        return getMethodDescriptor(type.parameterTypes, type.returnType);
    }

    private String getMethodDescriptor(Type[] parameterTypes, Type returnType) {
        StringBuilder stringBuilder = new StringBuilder("(");
        for (Type parameterType : parameterTypes) {
            stringBuilder.append(getDescriptor(parameterType));
        }
        return stringBuilder.append(")").append(getDescriptor(returnType)).toString();
    }

    private String getHandleDescriptor(HandleType type) {
        if (type instanceof Type) {
            return getDescriptor((Type) type);
        } else if (type instanceof MethodType) {
            return getMethodDescriptor((MethodType) type);
        } else {
            throw new InternalCompileException(type.getClass().getName());
        }
    }

    private int getVersion(IntegerLiteral literal) {
        return getConstantValue2(literal).intValue() + 44;
    }

    private int getClassAccess(Modifier[] modifiers) {
        int access = 0;
        for (Modifier modifier : modifiers) {
            String keyword = modifier.keyword;
            if ("public".equals(keyword)) {
                access |= ACC_PUBLIC;
            } else if ("private".equals(keyword)) {
                access |= ACC_PRIVATE;
            } else if ("protected".equals(keyword)) {
                access |= ACC_PROTECTED;
            } else if ("static".equals(keyword)) {
                access |= ACC_STATIC;
            } else if ("final".equals(keyword)) {
                access |= ACC_FINAL;
            } else if ("class".equals(keyword)) {
                access |= ACC_SUPER;
            } else if ("interface".equals(keyword)) {
                access |= ACC_INTERFACE | ACC_ABSTRACT;
            } else if ("abstract".equals(keyword)) {
                access |= ACC_ABSTRACT;
            } else if ("synthetic".equals(keyword)) {
                access |= ACC_SYNTHETIC;
            } else if ("@interface".equals(keyword)) {
                access |= ACC_ANNOTATION | ACC_INTERFACE | ACC_ABSTRACT;
            } else if ("enum".equals(keyword)) {
                access |= ACC_ENUM | ACC_SUPER;
            } else {
                throw new CompileException("Access modifier " + keyword + " not allowed for class", modifier.getLocation());
            }
        }
        return access;
    }

    private String getSuperclass(ReferenceType superclass) {
        return superclass != null ? getInternalName2(superclass.identifiers) : "java/lang/Object";
    }

    private String[] getInterfaces(ReferenceType[] interfaces) {
        if (interfaces.length == 0) return null;
        List<String> l = new ArrayList<>();
        for (ReferenceType inte : interfaces) {
            l.add(getInternalName2(inte.identifiers));
        }
        return l.toArray(EMPTY_STRING_ARRAY);
    }

    private void compileSource(SourceDeclaration sd, ClassFile cf) {
        if (sd == null) return;
        cf.visitSource(getConstantValue2(sd.file), null);
    }

    private void compileInnerClasses(List<InnerClassDeclaration> innerClasses, ClassFile cf) {
        for (InnerClassDeclaration innerClass : innerClasses) {
            String name = getInternalName2(innerClass.name);
            String innerName = innerClass.innerName;
            int access = getInnerClassAccess(innerClass.modifiers);

            // 根据类型调整访问标志
            switch (innerClass.type) {
                case ANONYMOUS:
                    cf.visitInnerClass(name, null, null, access);
                    break;
                case LOCAL:
                    cf.visitInnerClass(name, null, innerName, access);
                    break;
                case MEMBER_OR_STATIC:
                    String outerName = name.substring(0, name.length() - innerName.length() - 1);
                    cf.visitInnerClass(name, outerName, innerName, access);
                    break;
            }
        }
    }

    private int getInnerClassAccess(Modifier[] modifiers) {
        int access = 0;
        for (Modifier modifier : modifiers) {
            String keyword = modifier.keyword;
            if ("public".equals(keyword)) {
                access |= ACC_PUBLIC;
            } else if ("private".equals(keyword)) {
                access |= ACC_PRIVATE;
            } else if ("protected".equals(keyword)) {
                access |= ACC_PROTECTED;
            } else if ("static".equals(keyword)) {
                access |= ACC_STATIC;
            } else if ("final".equals(keyword)) {
                access |= ACC_FINAL;
            } else if ("interface".equals(keyword)) {
                access |= ACC_INTERFACE | ACC_ABSTRACT;
            } else if ("abstract".equals(keyword)) {
                access |= ACC_ABSTRACT;
            } else if ("synthetic".equals(keyword)) {
                access |= ACC_SYNTHETIC;
            } else if ("@interface".equals(keyword)) {
                access |= ACC_ANNOTATION | ACC_INTERFACE | ACC_ABSTRACT;
            } else if ("enum".equals(keyword)) {
                access |= ACC_ENUM;
            } else {
                throw new CompileException("Access modifier " + keyword + " not allowed for inner class", modifier.getLocation());
            }
        }
        return access;
    }

    private void compileFields(List<FieldDeclaration> fields, ClassFile cf) {
        for (FieldDeclaration field : fields) {
            compileField(field, cf);
        }
    }

    private void compileField(FieldDeclaration field, ClassFile cf) {
        int access = getFieldAccess(field.modifiers);
        String name = field.name;
        String descriptor = getDescriptor(field.type);
        Object value = field.value != null ? getConstantValue(field.value) : null;
        FieldVisitor fv = cf.visitField(access, name, descriptor, null, value); // TODO: signature

        compileAnnotations(field.annotations, fv);
        fv.visitEnd();
    }

    private int getFieldAccess(Modifier[] modifiers) {
        int access = 0;
        for (Modifier modifier : modifiers) {
            String keyword = modifier.keyword;
            if ("public".equals(keyword)) {
                access |= ACC_PUBLIC;
            } else if ("private".equals(keyword)) {
                access |= ACC_PRIVATE;
            } else if ("protected".equals(keyword)) {
                access |= ACC_PROTECTED;
            } else if ("static".equals(keyword)) {
                access |= ACC_STATIC;
            } else if ("final".equals(keyword)) {
                access |= ACC_FINAL;
            } else if ("volatile".equals(keyword)) {
                access |= ACC_VOLATILE;
            } else if ("transient".equals(keyword)) {
                access |= ACC_TRANSIENT;
            } else if ("synthetic".equals(keyword)) {
                access |= ACC_SYNTHETIC;
            } else if ("enum".equals(keyword)) {
                access |= ACC_ENUM;
            } else if ("mandated".equals(keyword)) {
                access |= ACC_MANDATED;
            } else {
                throw new CompileException("Access modifier " + keyword + " not allowed for field", modifier.getLocation());
            }
        }
        return access;
    }

    private void compileMethods(List<MethodDeclaration> methods, ClassFile cf) {
        for (MethodDeclaration method : methods) {
            compileMethod(method, cf);
        }
    }

    private String[] getMethodExceptions(ReferenceType[] types) {
        List<String> l = new ArrayList<>();
        for (ReferenceType type : types) {
            l.add(getInternalName2(type));
        }
        return l.toArray(EMPTY_STRING_ARRAY);
    }

    private void compileMethod(MethodDeclaration method, ClassFile cf) {
        int access = getMethodAccess(method.modifiers);
        String name = method.name;
        String descriptor = getMethodDescriptor(method.parameterTypes, method.returnType);
        String[] exceptions = getMethodExceptions(method.exceptionTypes);

        MethodVisitor mv = cf.visitMethod(access, name, descriptor, null, exceptions); // TODO: signature

        compileAnnotations(method.annotations, mv);
        compileMethodDefaultValue(method.defaultValue, mv);

        if (method.body != null) {
            mv.visitCode();
            compileBlock(method.body, null, mv);
            mv.visitMaxs(0, 0);
        }

        mv.visitEnd();
    }

    private int getMethodAccess(Modifier[] modifiers) {
        int access = 0;
        for (Modifier modifier : modifiers) {
            String keyword = modifier.keyword;
            if ("public".equals(keyword)) {
                access |= ACC_PUBLIC;
            } else if ("private".equals(keyword)) {
                access |= ACC_PRIVATE;
            } else if ("protected".equals(keyword)) {
                access |= ACC_PROTECTED;
            } else if ("static".equals(keyword)) {
                access |= ACC_STATIC;
            } else if ("final".equals(keyword)) {
                access |= ACC_FINAL;
            } else if ("synchronized".equals(keyword)) {
                access |= ACC_SYNCHRONIZED;
            } else if ("bridge".equals(keyword)) {
                access |= ACC_BRIDGE;
            } else if ("varargs".equals(keyword)) {
                access |= ACC_VARARGS;
            } else if ("native".equals(keyword)) {
                access |= ACC_NATIVE;
            } else if ("abstract".equals(keyword)) {
                access |= ACC_ABSTRACT;
            } else if ("strictfp".equals(keyword)) {
                access |= ACC_STRICT;
            } else if ("synthetic".equals(keyword)) {
                access |= ACC_SYNTHETIC;
            } else if ("mandated".equals(keyword)) {
                access |= ACC_MANDATED;
            } else {
                throw new CompileException("Access modifier " + keyword + " not allowed for method", modifier.getLocation());
            }
        }
        return access;
    }

    private void compileMethodDefaultValue(AnnotationValue defaultValue, MethodVisitor mv) {
        if (defaultValue != null) {
            compileAnnotationValue(null, defaultValue, mv.visitAnnotationDefault());
        }
    }

    private LabelMap buildLabelMap(LabelMap parent, List<Statement> statements) {
        Map<String, org.objectweb.asm.Label> map = new HashMap<>();
        for (Statement statement : statements) {
            if (statement instanceof Label) {
                Label labelInsn = (Label) statement;
                org.objectweb.asm.Label label = new org.objectweb.asm.Label();
                if (map.putIfAbsent(labelInsn.name, label) != null) {
                    throw new CompileException("Duplicate label: " + labelInsn.name, labelInsn.getLocation());
                }
            }
        }
        return new LabelMap(parent, map);
    }

    private void compileStatement(Statement statement, LabelMap labelMap, MethodVisitor mv) {
        if (statement instanceof Insn) {
            compileInsn((Insn) statement, mv);
        } else if (statement instanceof IntInsn) {
            compileIntInsn((IntInsn) statement, mv);
        } else if (statement instanceof VarInsn) {
            compileVarInsn((VarInsn) statement, mv);
        } else if (statement instanceof TypeInsn) {
            compileTypeInsn((TypeInsn) statement, mv);
        } else if (statement instanceof FieldInsn) {
            compileFieldInsn((FieldInsn) statement, mv);
        } else if (statement instanceof MethodInsn) {
            compileMethodInsn((MethodInsn) statement, mv);
        } else if (statement instanceof InvokeDynamicInsn) {
            compileInvokeDynamicInsn((InvokeDynamicInsn) statement, mv);
        } else if (statement instanceof JumpInsn) {
            compileJumpInsn((JumpInsn) statement, labelMap, mv);
        } else if (statement instanceof Label) {
            compileLabelInsn((Label) statement, labelMap, mv);
        } else if (statement instanceof LdcInsn) {
            compileLdcInsn((LdcInsn) statement, mv);
        } else if (statement instanceof IincInsn) {
            compileIincInsn((IincInsn) statement, mv);
        } else if (statement instanceof SwitchInsn) {
            compileSwitchInsn((SwitchInsn) statement, labelMap, mv);
        } else if (statement instanceof NewArrayInsn) {
            compileNewArrayInsn((NewArrayInsn) statement, mv);
        } else if (statement instanceof MultiANewArrayInsn) {
            compileMultiANewArrayInsn((MultiANewArrayInsn) statement, mv);
        } else if (statement instanceof Block) {
            compileBlock((Block) statement, labelMap, mv);
        } else if (statement instanceof LineNumber) {
            compileLineNumber((LineNumber) statement, labelMap, mv);
        } else if (statement instanceof LocalVariable) {
            compileLocalVariable((LocalVariable) statement, labelMap, mv);
        } else if (statement instanceof TryCatchBlock) {
            compileTryCatchBlock((TryCatchBlock) statement, labelMap, mv);
        } else {
            throw new InternalCompileException(statement.getClass().getName());
        }
    }

    private void compileInsn(Insn insn, MethodVisitor mv) {
        mv.visitInsn(getOpcode(insn.opcode));
    }

    private void compileIntInsn(IntInsn insn, MethodVisitor mv) {
        mv.visitIntInsn(getOpcode(insn.opcode), getConstantValue2(insn.operand).intValue());
    }

    private void compileVarInsn(VarInsn insn, MethodVisitor mv) {
        mv.visitVarInsn(getOpcode(insn.opcode), getConstantValue2(insn.var).intValue());
    }

    private void compileTypeInsn(TypeInsn insn, MethodVisitor mv) {
        mv.visitTypeInsn(getOpcode(insn.opcode), getInternalName(insn.type));
    }

    private void compileFieldInsn(FieldInsn insn, MethodVisitor mv) {
        mv.visitFieldInsn(getOpcode(insn.opcode), getInternalName2(insn.owner), insn.name, getDescriptor(insn.type));
    }

    private void compileMethodInsn(MethodInsn insn, MethodVisitor mv) {
        int opcode = getOpcode(insn.opcode);
        boolean isInterface = (opcode & FLAG_INTERFACE) != 0;
        if (isInterface) {
            opcode &= 0xFF;
        }
        mv.visitMethodInsn(opcode, getInternalName(insn.owner), insn.name, getMethodDescriptor(insn.methodType.parameterTypes, insn.methodType.returnType), isInterface);
    }

    private void compileInvokeDynamicInsn(InvokeDynamicInsn insn, MethodVisitor mv) {
        String name = insn.name;
        String descriptor = getMethodDescriptor(insn.methodType);
        org.objectweb.asm.Handle bootstrapMethod = getConstantValue2(insn.bootstrapMethod);
        Object[] bootstrapMethodArguments = getConstantValues(insn.bootstrapMethodArguments);
        mv.visitInvokeDynamicInsn(name, descriptor, bootstrapMethod, bootstrapMethodArguments);
    }

    private void compileJumpInsn(JumpInsn insn, LabelMap labelMap, MethodVisitor mv) {
        org.objectweb.asm.Label label = labelMap.get(insn.label);
        if (label == null) {
            throw new CompileException("Undefined label: " + insn.label, insn.getLocation());
        }
        mv.visitJumpInsn(getOpcode(insn.opcode), label);
    }

    private void compileLabelInsn(Label label, LabelMap labelMap, MethodVisitor mv) {
        mv.visitLabel(labelMap.get(label.name));
    }

    private void compileLdcInsn(LdcInsn insn, MethodVisitor mv) {
        Object constantValue = getConstantValue(insn.value);
        if (constantValue instanceof Boolean) {
            push((Boolean) constantValue, mv);
        } else if (constantValue instanceof Character) {
            push((Character) constantValue, mv);
        } else if (constantValue instanceof Integer) {
            push((Integer) constantValue, mv);
        } else if (constantValue instanceof Long) {
            push((Long) constantValue, mv);
        } else if (constantValue instanceof Float) {
            push((Float) constantValue, mv);
        } else if (constantValue instanceof Double) {
            push((Double) constantValue, mv);
        } else if (constantValue instanceof String) {
            push((String) constantValue, mv);
        } else if (constantValue instanceof org.objectweb.asm.Type) {
            push((org.objectweb.asm.Type) constantValue, mv);
        } else if (constantValue instanceof org.objectweb.asm.Handle) {
            push((org.objectweb.asm.Handle) constantValue, mv);
        } else if (constantValue instanceof org.objectweb.asm.ConstantDynamic) {
            push((org.objectweb.asm.ConstantDynamic) constantValue, mv);
        } else {
            throw new InternalCompileException(constantValue.getClass().getName());
        }
    }

    private void compileIincInsn(IincInsn insn, MethodVisitor mv) {
        mv.visitIincInsn(getConstantValue2(insn.var).intValue(), getConstantValue2(insn.increment).intValue());
    }

    private void compileSwitchInsn(SwitchInsn insn, LabelMap labelMap, MethodVisitor mv) {
        TreeMap<Integer, org.objectweb.asm.Label> keyLabelMap = new TreeMap<>();
        for (SwitchCase cas : insn.cases) {
            if (keyLabelMap.putIfAbsent(getConstantValue2(cas.key).intValue(), labelMap.get(cas.label)) != null) {
                throw new CompileException("Duplicate switch case: " + cas.key, insn.getLocation());
            }
        }

        org.objectweb.asm.Label dflt = labelMap.get(insn.dflt);
        int min = keyLabelMap.firstKey();
        int max = keyLabelMap.lastKey();
        int keyLabelMapSize = keyLabelMap.size();
        if (min + keyLabelMapSize >= max - keyLabelMapSize) {
            int size = max - min + 1;
            org.objectweb.asm.Label[] labels = new org.objectweb.asm.Label[size];
            for (int i = 0; i < size; i++) {
                org.objectweb.asm.Label label = keyLabelMap.get(min + i);
                labels[i] = label != null ? label : dflt;
            }
            mv.visitTableSwitchInsn(min, max, dflt, labels);
        } else {
            int[] keys = new int[keyLabelMapSize];
            org.objectweb.asm.Label[] labels = new org.objectweb.asm.Label[keyLabelMapSize];
            int i = 0;
            for (Map.Entry<Integer, org.objectweb.asm.Label> keyLabel : keyLabelMap.entrySet()) {
                keys[i] = keyLabel.getKey();
                labels[i] = keyLabel.getValue();
                i++;
            }
            mv.visitLookupSwitchInsn(dflt, keys, labels);
        }
    }

    private void compileNewArrayInsn(NewArrayInsn insn, MethodVisitor mv) {
        mv.visitIntInsn(NEWARRAY, getNewArrayInsnOperand(insn.type));
    }

    private static int getNewArrayInsnOperand(PrimitiveType type) {
        switch (type.primitive) {
            case BOOLEAN:
                return T_BOOLEAN;
            case CHAR:
                return T_CHAR;
            case FLOAT:
                return T_FLOAT;
            case DOUBLE:
                return T_DOUBLE;
            case BYTE:
                return T_BYTE;
            case SHORT:
                return T_SHORT;
            case INT:
                return T_INT;
            case LONG:
                return T_LONG;
            default:
                throw new InternalCompileException();
        }
    }

    private void compileMultiANewArrayInsn(MultiANewArrayInsn insn, MethodVisitor mv) {
        mv.visitMultiANewArrayInsn(getDescriptor(insn.type), getConstantValue2(insn.numDimensions).intValue());
    }

    private void compileBlock(Block block, LabelMap parentLabelMap, MethodVisitor mv) {
        List<Statement> statements = block.statements;
        LabelMap labelMap = buildLabelMap(parentLabelMap, statements);
        for (Statement statement : statements) {
            compileStatement(statement, labelMap, mv);
        }
    }

    private void compileLineNumber(LineNumber lineNumber, LabelMap labelMap, MethodVisitor mv) {
        org.objectweb.asm.Label label = labelMap.get(lineNumber.label);
        if (label == null) {
            throw new CompileException("Undefined label: " + lineNumber.label, lineNumber.getLocation());
        }
        mv.visitLineNumber(getConstantValue2(lineNumber.line).intValue(), label);
    }

    private void compileLocalVariable(LocalVariable localVariable, LabelMap labelMap, MethodVisitor mv) {
        org.objectweb.asm.Label start = labelMap.get(localVariable.start);
        if (start == null) {
            throw new CompileException("Undefined start label: " + localVariable.start, localVariable.getLocation());
        }
        org.objectweb.asm.Label end = labelMap.get(localVariable.end);
        if (end == null) {
            throw new CompileException("Undefined end label: " + localVariable.end, localVariable.getLocation());
        }
        mv.visitLocalVariable(
                localVariable.name,
                getDescriptor(localVariable.type),
                null, // TODO: signature
                start,
                end,
                getConstantValue2(localVariable.index).intValue());
    }

    private void compileTryCatchBlock(TryCatchBlock tryCatchBlock, LabelMap labelMap, MethodVisitor mv) {
        org.objectweb.asm.Label start = labelMap.get(tryCatchBlock.start);
        if (start == null) {
            throw new CompileException("Undefined start label: " + tryCatchBlock.start, tryCatchBlock.getLocation());
        }
        org.objectweb.asm.Label end = labelMap.get(tryCatchBlock.end);
        if (end == null) {
            throw new CompileException("Undefined end label: " + tryCatchBlock.end, tryCatchBlock.getLocation());
        }
        org.objectweb.asm.Label handler = labelMap.get(tryCatchBlock.handler);
        if (handler == null) {
            throw new CompileException("Undefined handler label: " + tryCatchBlock.handler, tryCatchBlock.getLocation());
        }
        mv.visitTryCatchBlock(
                start,
                end,
                handler,
                tryCatchBlock.type != null ? getInternalName2(tryCatchBlock.type) : null);
    }

    private void compileAnnotations(Annotation[] annotations, ClassFile cf) {
        for (Annotation annotation : annotations) {
            compileAnnotation(annotation, cf.visitAnnotation(getDescriptor2(annotation.type), annotation.visible));
        }
    }

    private void compileAnnotations(Annotation[] annotations, FieldVisitor fv) {
        for (Annotation annotation : annotations) {
            compileAnnotation(annotation, fv.visitAnnotation(getDescriptor2(annotation.type), annotation.visible));
        }
    }

    private void compileAnnotations(Annotation[] annotations, MethodVisitor mv) {
        for (Annotation annotation : annotations) {
            compileAnnotation(annotation, mv.visitAnnotation(getDescriptor2(annotation.type), annotation.visible));
        }
    }

    private void compileAnnotation(Annotation annotation, AnnotationVisitor av) {
        for (AnnotationValuePair pair : annotation.pairs) {
            compileAnnotationValue(pair.key, pair.value, av);
        }
        av.visitEnd();
    }

    private void compileAnnotationValue(String key, AnnotationValue value, AnnotationVisitor av) {
        if (value instanceof Annotation) {
            Annotation annotation = (Annotation) value;
            compileAnnotation(annotation, av.visitAnnotation(key, getDescriptor(annotation.type)));
        } else if (value instanceof AnnotationValueArrayInitializer) {
            compileAnnotationValueArray((AnnotationValueArrayInitializer) value, av.visitArray(key));
        } else if (value instanceof EnumLiteral) {
            EnumLiteral el = (EnumLiteral) value;
            av.visitEnum(key, getDescriptor2(el.owner), el.name);
        } else if (value instanceof Value) {
            av.visit(key, getConstantValue((Value) value));
        } else {
            throw new InternalCompileException("annotation value");
        }
    }

    private void compileAnnotationValueArray(AnnotationValueArrayInitializer ava, AnnotationVisitor av) {
        for (AnnotationValue value : ava.values) {
            compileAnnotationValue(null, value, av);
        }
        av.visitEnd();
    }

    private Object getConstantValue(Value value) {
        if (value instanceof StringLiteral) {
            return getConstantValue2((StringLiteral) value);
        } else if (value instanceof IntegerLiteral) {
            return getConstantValue2((IntegerLiteral) value);
        } else if (value instanceof FloatingPointLiteral) {
            return getConstantValue2((FloatingPointLiteral) value);
        } else if (value instanceof BooleanLiteral) {
            return getConstantValue2((BooleanLiteral) value);
        } else if (value instanceof CharacterLiteral) {
            return getConstantValue2((CharacterLiteral) value);
        } else if (value instanceof NullLiteral) {
            return getConstantValue2((NullLiteral) value);
        } else if (value instanceof Type) {
            return getConstantValue2((Type) value);
        } else if (value instanceof MethodType) {
            return getConstantValue2((MethodType) value);
        } else if (value instanceof Handle) {
            return getConstantValue2((Handle) value);
        } else if (value instanceof ConstantDynamic) {
            return getConstantValue2((ConstantDynamic) value);
        } else {
            throw new InternalCompileException(value.getClass().getName());
        }
    }

    private String getConstantValue2(StringLiteral sl) {
        String v = sl.value;
        return unescape(v.substring(1, v.length() - 1), sl.getLocation());
    }

    private Number getConstantValue2(IntegerLiteral il) {
        String v = il.value.toLowerCase();

        v = removeUnderscore(v);

        int radix;
        boolean signed;

        if (v.startsWith("0x")) {
            radix = 16;
            signed = false;
            v = v.substring(2);
        } else if (v.startsWith("0b")) {
            radix = 2;
            signed = false;
            v = v.substring(2);
        } else if (v.startsWith("0") && !"0".equals(v) && !"0l".equals(v)) {
            radix = 8;
            signed = false;
            v = v.substring(1);
        } else {
            radix = 10;
            signed = true;
        }

        if (v.endsWith("b")) {
            v = v.substring(0, v.length() - 1);
            return signed ? Byte.parseByte(v, radix) : parseUnsignedByte(v, radix);
        } else if (v.endsWith("s")) {
            v = v.substring(0, v.length() - 1);
            return signed ? Short.parseShort(v, radix) : parseUnsignedShort(v, radix);
        } else if (v.endsWith("l")) {
            v = v.substring(0, v.length() - 1);
            return signed ? Long.parseLong(v, radix) : Long.parseUnsignedLong(v, radix);
        } else {
            return signed ? Integer.parseInt(v, radix) : Integer.parseUnsignedInt(v, radix);
        }
    }

    private static byte parseUnsignedByte(String s, int radix) {
        int uint = Integer.parseUnsignedInt(s, radix);
        if ((uint & 0xFFFF_FF00) == 0) {
            return (byte) uint;
        } else {
            throw new NumberFormatException("String value " + s + " exceeds range of unsigned byte.");
        }
    }

    private static short parseUnsignedShort(String s, int radix) {
        int uint = Integer.parseUnsignedInt(s, radix);
        if ((uint & 0xFFFF_0000) == 0) {
            return (short) uint;
        } else {
            throw new NumberFormatException("String value " + s + " exceeds range of unsigned short.");
        }
    }

    private Number getConstantValue2(FloatingPointLiteral fpl) {
        String v = fpl.value.toLowerCase();

        v = removeUnderscore(v);

        if (v.endsWith("f")) {
            v = v.substring(0, v.length() - 1);

            float fv;
            try {
                fv = Float.parseFloat(v);
            } catch (NumberFormatException e) {
                throw new InternalCompileException(fpl.value);
            }
            if (Float.isInfinite(fv)) {
                throw new CompileException("Value of float literal \"" + fpl.value + "\" is out of range", null);
            }
            if (Float.isNaN(fv)) {
                throw new InternalCompileException(fpl.value);
            }
            return fv;
        }

        if (v.endsWith("d")) v = v.substring(0, v.length() - 1);

        double dv;
        try {
            dv = Double.parseDouble(v);
        } catch (NumberFormatException e) {
            throw new InternalCompileException(fpl.value);
        }
        if (Double.isInfinite(dv)) {
            throw new CompileException("Value of double literal \"" + fpl.value + "\" is out of range", null);
        }
        if (Double.isNaN(dv)) {
            throw new InternalCompileException(fpl.value);
        }
        return dv;
    }

    private static String removeUnderscore(String s) {
        int i = s.indexOf('_');
        if (i == -1) return s;

        int j = 0;
        StringBuilder sb = new StringBuilder();
        do {
            sb.append(s, j, i);
            j = i + 1;
            i = s.indexOf('_', j);
        } while (i != -1);
        return sb.append(s, j, s.length()).toString();
    }

    private Boolean getConstantValue2(BooleanLiteral bl) {
        if ("true".equals(bl.value)) return true;
        if ("false".equals(bl.value)) return false;
        throw new InternalCompileException(bl.value);
    }

    private Character getConstantValue2(CharacterLiteral cl) {
        String v = cl.value;

        v = unescape(v.substring(1, v.length() - 1), cl.getLocation());

        return v.charAt(0);
    }

    private Object getConstantValue2(NullLiteral nl) {
        return null;
    }

    private org.objectweb.asm.Type getConstantValue2(Type t) {
        return org.objectweb.asm.Type.getType(getDescriptor(t));
    }

    private org.objectweb.asm.Type getConstantValue2(MethodType mt) {
        return org.objectweb.asm.Type.getMethodType(getMethodDescriptor(mt));
    }

    private org.objectweb.asm.Handle getConstantValue2(Handle h) {
        int kind = getHandleKind(h.kind);
        String owner = getInternalName2(h.owner);
        String name = h.name;
        String descriptor = getHandleDescriptor(h.type);
        boolean isInterface = (kind & FLAG_INTERFACE) != 0;
        return new org.objectweb.asm.Handle(kind & 0xFF, owner, name, descriptor, isInterface);
    }

    private org.objectweb.asm.ConstantDynamic getConstantValue2(ConstantDynamic cd) {
        String name = cd.name;
        String descriptor = getDescriptor(cd.type);
        org.objectweb.asm.Handle bootstrapMethod = getConstantValue2(cd.bootstrapMethod);
        Object[] bootstrapMethodArguments = getConstantValues(cd.bootstrapMethodArguments);
        return new org.objectweb.asm.ConstantDynamic(name, descriptor, bootstrapMethod, bootstrapMethodArguments);
    }

    private Object[] getConstantValues(Value[] values) {
        Object[] a = new Object[values.length];
        for (int i = 0; i < values.length; i++) {
            a[i] = getConstantValue(values[i]);
        }
        return a;
    }

    private static void push(boolean value, MethodVisitor mv) {
        push(value ? 1 : 0, mv);
    }

    private static void push(int value, MethodVisitor mv) {
        if (value >= -1 && value <= 5) {
            mv.visitInsn(Opcodes.ICONST_0 + value);
        } else if (value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE) {
            mv.visitIntInsn(Opcodes.BIPUSH, value);
        } else if (value >= Short.MIN_VALUE && value <= Short.MAX_VALUE) {
            mv.visitIntInsn(Opcodes.SIPUSH, value);
        } else {
            mv.visitLdcInsn(value);
        }
    }

    private static void push(long value, MethodVisitor mv) {
        if (value == 0L || value == 1L) {
            mv.visitInsn(Opcodes.LCONST_0 + (int) value);
        } else {
            mv.visitLdcInsn(value);
        }
    }

    private static void push(float value, MethodVisitor mv) {
        int bits = Float.floatToIntBits(value);
        if (bits == 0L || bits == 0x3F800000 || bits == 0x40000000) { // 0..2
            mv.visitInsn(Opcodes.FCONST_0 + (int) value);
        } else {
            mv.visitLdcInsn(value);
        }
    }

    private static void push(double value, MethodVisitor mv) {
        long bits = Double.doubleToLongBits(value);
        if (bits == 0L || bits == 0x3FF0000000000000L) { // +0.0d and 1.0d
            mv.visitInsn(Opcodes.DCONST_0 + (int) value);
        } else {
            mv.visitLdcInsn(value);
        }
    }

    private static void push(String value, MethodVisitor mv) {
        if (value == null) {
            mv.visitInsn(Opcodes.ACONST_NULL);
        } else {
            mv.visitLdcInsn(value);
        }
    }

    private static final String CLASS_DESCRIPTOR = "Ljava/lang/Class;";

    private static void push(org.objectweb.asm.Type value, MethodVisitor mv) {
        if (value == null) {
            mv.visitInsn(Opcodes.ACONST_NULL);
        } else {
            switch (value.getSort()) {
                case org.objectweb.asm.Type.VOID:
                    mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/Void", "TYPE", CLASS_DESCRIPTOR);
                    break;
                case org.objectweb.asm.Type.BOOLEAN:
                    mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/Boolean", "TYPE", CLASS_DESCRIPTOR);
                    break;
                case org.objectweb.asm.Type.CHAR:
                    mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/Character", "TYPE", CLASS_DESCRIPTOR);
                    break;
                case org.objectweb.asm.Type.BYTE:
                    mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/Byte", "TYPE", CLASS_DESCRIPTOR);
                    break;
                case org.objectweb.asm.Type.SHORT:
                    mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/Short", "TYPE", CLASS_DESCRIPTOR);
                    break;
                case org.objectweb.asm.Type.INT:
                    mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/Integer", "TYPE", CLASS_DESCRIPTOR);
                    break;
                case org.objectweb.asm.Type.FLOAT:
                    mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/Float", "TYPE", CLASS_DESCRIPTOR);
                    break;
                case org.objectweb.asm.Type.LONG:
                    mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/Long", "TYPE", CLASS_DESCRIPTOR);
                    break;
                case org.objectweb.asm.Type.DOUBLE:
                    mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/Double", "TYPE", CLASS_DESCRIPTOR);
                    break;
                default:
                    mv.visitLdcInsn(value);
                    break;
            }
        }
    }

    private static void push(org.objectweb.asm.Handle handle, MethodVisitor mv) {
        if (handle == null) {
            mv.visitInsn(Opcodes.ACONST_NULL);
        } else {
            mv.visitLdcInsn(handle);
        }
    }

    private static void push(org.objectweb.asm.ConstantDynamic constantDynamic, MethodVisitor mv) {
        if (constantDynamic == null) {
            mv.visitInsn(Opcodes.ACONST_NULL);
        } else {
            mv.visitLdcInsn(constantDynamic);
        }
    }

    private static String unescape(String s, Location location) {
        int i = s.indexOf('\\');
        if (i == -1) {
            return s;
        }

        int length = s.length();
        StringBuilder sb = new StringBuilder(length).append(s, 0, i);
        while (i < length) {
            char c = s.charAt(i++);
            if (c != '\\') {
                sb.append(c);
                continue;
            }

            c = s.charAt(i++);
            {
                int idx = "btnfr\"'\\".indexOf(c);
                if (idx != -1) {
                    sb.append("\b\t\n\f\r\"'\\".charAt(idx));
                    continue;
                }
            }

            int x = Character.digit(c, 8);
            if (x == -1) throw new CompileException("Invalid escape sequence \"\\" + c + "\"", location);

            if (i < s.length()) {
                c = s.charAt(i);
                int secondDigit = Character.digit(c, 8);
                if (secondDigit != -1) {
                    x = (x << 3) + secondDigit;
                    i++;
                    if (i < s.length() && x <= 037) {
                        c = s.charAt(i);
                        int thirdDigit = Character.digit(c, 8);
                        if (thirdDigit != -1) {
                            x = (x << 3) + thirdDigit;
                            i++;
                        }
                    }
                }
            }
            sb.append((char) x);
        }

        return sb.toString();
    }
}
