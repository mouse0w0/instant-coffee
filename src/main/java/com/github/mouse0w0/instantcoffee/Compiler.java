package com.github.mouse0w0.instantcoffee;

import com.github.mouse0w0.instantcoffee.model.Type;
import com.github.mouse0w0.instantcoffee.model.*;
import com.github.mouse0w0.instantcoffee.model.insn.*;
import org.objectweb.asm.*;
import org.objectweb.asm.signature.SignatureVisitor;
import org.objectweb.asm.signature.SignatureWriter;

import java.util.*;

import static com.github.mouse0w0.instantcoffee.Constants.*;

public class Compiler {
    private static final String OBJECT = "java/lang/Object";
    private static final String DEPRECATED = "java/lang/Deprecated";

    public Compiler() {
    }

    public ClassFile compile(ClassDeclaration cd) {
        return compile(cd, new ClassFile());
    }

    public ClassFile compile(ClassDeclaration cd, ClassFile cf) {
        int version = getVersion(cd.version);
        int access = getAccess(cd.modifiers);
        String name = getInternalName(cd.identifiers);
        String signature = getClassSignature(cd.typeParameters, cd.superclass, cd.interfaces);
        String superclass = getSuperclass(cd.superclass);
        String[] interfaces = getInterfaces(cd.interfaces);
        cf.visit(version, access, name, signature, superclass, interfaces);
        compileSource(cd.source, cf);
        compileAnnotations(cd.annotations, cf);
        compileInnerClasses(cd.innerClasses, cf);
        compileFields(cd.fields, cf);
        compileMethods(cd.methods, cf);
        cf.visitEnd();
        return cf;
    }

    private String getInternalName(ReferenceType type) {
        return getInternalName(type.identifiers);
    }

    private String getInternalName(String[] identifiers) {
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
        } else {
            throw new InternalCompileException(type.getClass().getName());
        }
    }

    private String getDescriptor(String[] identifiers) {
        return "L" + getInternalName(identifiers) + ";";
    }

    private String getDescriptor2(PrimitiveType type) {
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

    private String getDescriptor2(ArrayType type) {
        return "[" + getDescriptor(type.componentType);
    }

    private String getDescriptor2(ReferenceType type) {
        return "L" + getInternalName(type.identifiers) + ";";
    }

    private String getMethodDescriptor(Type[] parameterTypes, Type returnType) {
        StringBuilder stringBuilder = new StringBuilder("(");
        for (Type parameterType : parameterTypes) {
            stringBuilder.append(getDescriptor(parameterType));
        }
        return stringBuilder.append(")").append(getDescriptor(returnType)).toString();
    }

    private int getVersion(IntegerLiteral literal) {
        return getConstantValue2(literal).intValue() + 44;
    }

    private int getAccess(Modifier[] modifiers) {
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
            } else if ("synchronized".equals(keyword)) {
                access |= ACC_SYNCHRONIZED;
                // } else if ("open".equals(keyword)) {
                //     access |= ACC_OPEN;
                // } else if ("transitive".equals(keyword)) {
                //     access |= ACC_TRANSITIVE;
            } else if ("volatile".equals(keyword)) {
                access |= ACC_VOLATILE;
            } else if ("bridge".equals(keyword)) {
                access |= ACC_BRIDGE;
                // } else if ("static".equals(keyword)) {
                //     access |= ACC_STATIC_PHASE;
            } else if ("varargs".equals(keyword)) {
                access |= ACC_VARARGS;
            } else if ("transient".equals(keyword)) {
                access |= ACC_TRANSIENT;
            } else if ("native".equals(keyword)) {
                access |= ACC_NATIVE;
            } else if ("interface".equals(keyword)) {
                access |= ACC_INTERFACE | ACC_ABSTRACT;
            } else if ("abstract".equals(keyword)) {
                access |= ACC_ABSTRACT;
            } else if ("strictfp".equals(keyword)) {
                access |= ACC_STRICT;
            } else if ("synthetic".equals(keyword)) {
                access |= ACC_SYNTHETIC;
            } else if ("@interface".equals(keyword)) {
                access |= ACC_ANNOTATION | ACC_INTERFACE | ACC_ABSTRACT;
            } else if ("enum".equals(keyword)) {
                access |= ACC_ENUM | ACC_SUPER | ACC_FINAL;
            } else if ("mandated".equals(keyword)) {
                access |= ACC_MANDATED;
            } else if ("module".equals(keyword)) {
                access |= ACC_MODULE;
            } else if ("record".equals(keyword)) {
                access |= ACC_RECORD | ACC_SUPER | ACC_FINAL;
            } else if ("deprecated".equals(keyword)) {
                access |= ACC_DEPRECATED;
            } else {
                throw new InternalCompileException(keyword);
            }
        }
        return access;
    }

    private String getSuperclass(ReferenceType superclass) {
        return superclass != null ? getInternalName(superclass.identifiers) : OBJECT;
    }

    private String[] getInterfaces(ReferenceType[] interfaces) {
        if (interfaces.length == 0) return null;
        List<String> l = new ArrayList<>();
        for (ReferenceType inte : interfaces) {
            l.add(getInternalName(inte.identifiers));
        }
        return l.toArray(EMPTY_STRING_ARRAY);
    }

    private void compileSource(SourceDeclaration sd, ClassFile cf) {
        if (sd == null) return;
        cf.visitSource(getConstantValue2(sd.file), null);
    }

    private void compileInnerClasses(List<InnerClassDeclaration> innerClasses, ClassFile cf) {
        for (InnerClassDeclaration innerClass : innerClasses) {
            String outerName = getInternalName(innerClass.outerName);
            String innerName = innerClass.innerName;
            String name = outerName + "$" + innerName;
            int access = getAccess(innerClass.modifiers);
            cf.visitInnerClass(name, outerName, innerName, access);
        }
    }

    private void compileFields(List<FieldDeclaration> fields, ClassFile cf) {
        for (FieldDeclaration field : fields) {
            compileField(field, cf);
        }
    }

    private void compileField(FieldDeclaration field, ClassFile cf) {
        int access = getAccess(field.modifiers);
        String name = field.name;
        String descriptor = getDescriptor(field.type);
        String signature = getTypeSignature(field.type);
        Object value = field.value != null ? getConstantValue(field.value) : null;
        FieldVisitor fv = cf.visitField(access, name, descriptor, signature, value);

        compileAnnotations(field.annotations, fv);
        fv.visitEnd();
    }

    private void compileMethods(List<MethodDeclaration> methods, ClassFile cf) {
        for (MethodDeclaration method : methods) {
            compileMethod(method, cf);
        }
    }

    private String[] getMethodExceptions(ReferenceType[] types) {
        List<String> l = new ArrayList<>();
        for (ReferenceType type : types) {
            l.add(getInternalName(type));
        }
        return l.toArray(EMPTY_STRING_ARRAY);
    }

    private void compileMethod(MethodDeclaration method, ClassFile cf) {
        int access = getAccess(method.modifiers);
        String name = method.name;
        String descriptor = getMethodDescriptor(method.parameterTypes, method.returnType);
        String signature = getMethodSignature(method.typeParameters, method.parameterTypes, method.returnType, method.exceptionTypes);
        String[] exceptions = getMethodExceptions(method.exceptionTypes);

        MethodVisitor mv = cf.visitMethod(access, name, descriptor, signature, exceptions);

        compileAnnotations(method.annotations, mv);
        compileMethodDefaultValue(method.defaultValue, mv);

        mv.visitCode();

        Map<String, Label> labels = getLabels(method.instructions);

        for (BaseInsn instruction : method.instructions) {
            compileInstruction(instruction, labels, mv);
        }

        for (LocalVariable localVariable : method.localVariables) {
            compileLocalVariable(localVariable, labels, mv);
        }

        for (TryCatchBlock tryCatchBlock : method.tryCatchBlocks) {
            compileTryCatchBlock(tryCatchBlock, labels, mv);
        }

        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    private void compileMethodDefaultValue(AnnotationValue defaultValue, MethodVisitor mv) {
        if (defaultValue != null) {
            compileAnnotationValue(null, defaultValue, mv.visitAnnotationDefault());
        }
    }

    private Map<String, Label> getLabels(List<BaseInsn> insnList) {
        Map<String, Label> labels = new HashMap<>();
        for (BaseInsn insn : insnList) {
            if (insn instanceof LabelInsn) {
                LabelInsn labelInsn = (LabelInsn) insn;
                Label label = new Label();
                if (labels.putIfAbsent(labelInsn.name, label) != null) {
                    throw new CompileException("Duplicate label: " + labelInsn.name, labelInsn.getLocation());
                }
            }
        }
        return labels;
    }

    private void compileInstruction(BaseInsn insn, Map<String, Label> labels, MethodVisitor mv) {
        if (insn instanceof Insn) {
            compileInsn((Insn) insn, mv);
        } else if (insn instanceof IntInsn) {
            compileIntInsn((IntInsn) insn, mv);
        } else if (insn instanceof VarInsn) {
            compileVarInsn((VarInsn) insn, mv);
        } else if (insn instanceof TypeInsn) {
            compileTypeInsn((TypeInsn) insn, mv);
        } else if (insn instanceof FieldInsn) {
            compileFieldInsn((FieldInsn) insn, mv);
        } else if (insn instanceof MethodInsn) {
            compileMethodInsn((MethodInsn) insn, mv);
        } else if (insn instanceof JumpInsn) {
            compileJumpInsn((JumpInsn) insn, labels, mv);
        } else if (insn instanceof LabelInsn) {
            compileLabelInsn((LabelInsn) insn, labels, mv);
        } else if (insn instanceof LdcInsn) {
            compileLdcInsn((LdcInsn) insn, mv);
        } else if (insn instanceof IincInsn) {
            compileIincInsn((IincInsn) insn, mv);
        } else if (insn instanceof SwitchInsn) {
            compileSwitchInsn((SwitchInsn) insn, labels, mv);
        } else if (insn instanceof MultiANewArrayInsn) {
            compileMultiANewArrayInsn((MultiANewArrayInsn) insn, mv);
        } else if (insn instanceof LineNumberInsn) {
            compileLineNumberInsn((LineNumberInsn) insn, labels, mv);
        } else {
            throw new InternalCompileException(insn.getClass().getName());
        }
    }

    private void compileInsn(Insn insn, MethodVisitor mv) {
        mv.visitInsn(nameToOpcode(insn.opcode));
    }

    private void compileIntInsn(IntInsn insn, MethodVisitor mv) {
        mv.visitVarInsn(nameToOpcode(insn.opcode), getConstantValue2(insn.operand).intValue());
    }

    private void compileVarInsn(VarInsn insn, MethodVisitor mv) {
        mv.visitVarInsn(nameToOpcode(insn.opcode), getConstantValue2(insn.var).intValue());
    }

    private void compileTypeInsn(TypeInsn insn, MethodVisitor mv) {
        mv.visitTypeInsn(nameToOpcode(insn.opcode), getInternalName(insn.type));
    }

    private void compileFieldInsn(FieldInsn insn, MethodVisitor mv) {
        mv.visitFieldInsn(nameToOpcode(insn.opcode), getInternalName(insn.owner), insn.name, getDescriptor(insn.type));
    }

    private void compileMethodInsn(MethodInsn insn, MethodVisitor mv) {
        int opcode = nameToOpcode(insn.opcode);
        boolean isInterface = opcode >= INVOKEINTERFACE;
        if (opcode >= INVOKEVIRTUALINTERFACE) {
            opcode -= (INVOKEVIRTUALINTERFACE - INVOKEVIRTUAL);
        }
        mv.visitMethodInsn(opcode, getInternalName(insn.owner), insn.name, getMethodDescriptor(insn.parameterTypes, insn.returnType), isInterface);
    }

    private void compileJumpInsn(JumpInsn insn, Map<String, Label> labels, MethodVisitor mv) {
        Label label = labels.get(insn.label);
        if (label == null) {
            throw new CompileException("Undefined label: " + insn.label, insn.getLocation());
        }
        mv.visitJumpInsn(nameToOpcode(insn.opcode), label);
    }

    private void compileLabelInsn(LabelInsn labelInsn, Map<String, Label> labels, MethodVisitor mv) {
        mv.visitLabel(labels.get(labelInsn.name));
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
        } else {
            throw new InternalCompileException(constantValue.getClass().getName());
        }
    }

    private void compileIincInsn(IincInsn insn, MethodVisitor mv) {
        mv.visitIincInsn(getConstantValue2(insn.var).intValue(), getConstantValue2(insn.increment).intValue());
    }

    private void compileSwitchInsn(SwitchInsn insn, Map<String, Label> labelMap, MethodVisitor mv) {
        TreeMap<Integer, Label> keyLabelMap = new TreeMap<>();
        for (SwitchCase cas : insn.cases) {
            if (keyLabelMap.putIfAbsent(getConstantValue2(cas.key).intValue(), labelMap.get(cas.label)) != null) {
                throw new CompileException("Duplicate switch case: " + cas.key, insn.getLocation());
            }
        }

        Label dflt = labelMap.get(insn.dflt);
        int min = keyLabelMap.firstKey();
        int max = keyLabelMap.lastKey();
        int keyLabelMapSize = keyLabelMap.size();
        if (min + keyLabelMapSize >= max - keyLabelMapSize) {
            int size = max - min + 1;
            Label[] labels = new Label[size];
            for (int i = 0; i < size; i++) {
                Label label = keyLabelMap.get(min + i);
                labels[i] = label != null ? label : dflt;
            }
            mv.visitTableSwitchInsn(min, max, dflt, labels);
        } else {
            int[] keys = new int[keyLabelMapSize];
            Label[] labels = new Label[keyLabelMapSize];
            int i = 0;
            for (Map.Entry<Integer, Label> keyLabel : keyLabelMap.entrySet()) {
                keys[i] = keyLabel.getKey();
                labels[i] = keyLabel.getValue();
                i++;
            }
            mv.visitLookupSwitchInsn(dflt, keys, labels);
        }
    }

    private void compileMultiANewArrayInsn(MultiANewArrayInsn insn, MethodVisitor mv) {
        mv.visitMultiANewArrayInsn(getDescriptor(insn.type), getConstantValue2(insn.numDimensions).intValue());
    }

    private void compileLineNumberInsn(LineNumberInsn insn, Map<String, Label> labels, MethodVisitor mv) {
        Label label = labels.get(insn.label);
        if (label == null) {
            throw new CompileException("Undefined label: " + insn.label, insn.getLocation());
        }
        mv.visitLineNumber(getConstantValue2(insn.line).intValue(), label);
    }

    private void compileLocalVariable(LocalVariable localVariable, Map<String, Label> labels, MethodVisitor mv) {
        Label start = labels.get(localVariable.start);
        if (start == null) {
            throw new CompileException("Undefined start label: " + localVariable.start, localVariable.getLocation());
        }
        Label end = labels.get(localVariable.end);
        if (end == null) {
            throw new CompileException("Undefined end label: " + localVariable.end, localVariable.getLocation());
        }
        mv.visitLocalVariable(
                localVariable.name,
                getDescriptor(localVariable.type),
                getTypeSignature(localVariable.type),
                start,
                end,
                getConstantValue2(localVariable.index).intValue());
    }

    private void compileTryCatchBlock(TryCatchBlock tryCatchBlock, Map<String, Label> labels, MethodVisitor mv) {
        Label start = labels.get(tryCatchBlock.start);
        if (start == null) {
            throw new CompileException("Undefined start label: " + tryCatchBlock.start, tryCatchBlock.getLocation());
        }
        Label end = labels.get(tryCatchBlock.end);
        if (end == null) {
            throw new CompileException("Undefined end label: " + tryCatchBlock.end, tryCatchBlock.getLocation());
        }
        Label handler = labels.get(tryCatchBlock.handler);
        if (handler == null) {
            throw new CompileException("Undefined handler label: " + tryCatchBlock.handler, tryCatchBlock.getLocation());
        }
        mv.visitTryCatchBlock(
                start,
                end,
                handler,
                tryCatchBlock.type != null ? getInternalName(tryCatchBlock.type) : null);
    }

    private void compileAnnotations(Annotation[] annotations, ClassFile cf) {
        for (Annotation annotation : annotations) {
            compileAnnotation(annotation, cf.visitAnnotation(getDescriptor(annotation.type), annotation.visible));
        }
    }

    private void compileAnnotations(Annotation[] annotations, FieldVisitor fv) {
        for (Annotation annotation : annotations) {
            compileAnnotation(annotation, fv.visitAnnotation(getDescriptor(annotation.type), annotation.visible));
        }
    }

    private void compileAnnotations(Annotation[] annotations, MethodVisitor mv) {
        for (Annotation annotation : annotations) {
            compileAnnotation(annotation, mv.visitAnnotation(getDescriptor(annotation.type), annotation.visible));
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
        } else if (value instanceof AmbiguousName) {
            AmbiguousName an = (AmbiguousName) value;
            String[] identifiers = an.identifiers;
            String descriptor = getDescriptor(Arrays.copyOf(identifiers, identifiers.length - 1));
            String name = identifiers[identifiers.length - 1];
            av.visitEnum(key, descriptor, name);
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
        if (value instanceof Cast) {
            return getConstantValue2((Cast) value);
        } else if (value instanceof StringLiteral) {
            return getConstantValue2((StringLiteral) value);
        } else if (value instanceof ClassLiteral) {
            return getConstantValue2((ClassLiteral) value);
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
        } else {
            throw new InternalCompileException(value.getClass().getName());
        }
    }

    private Object getConstantValue2(Cast cast) {
        Object v = getConstantValue(cast.value);

        if (v instanceof Number && cast.type instanceof PrimitiveType) {
            Number n = (Number) v;
            switch (((PrimitiveType) cast.type).primitive) {
                case CHAR:
                    return (char) n.intValue();
                case BYTE:
                    return n.byteValue();
                case SHORT:
                    return n.shortValue();
                case INT:
                    return n.intValue();
                case LONG:
                    return n.longValue();
                case FLOAT:
                    return n.floatValue();
                case DOUBLE:
                    return n.doubleValue();
            }
        }

        throw new CompileException("Cannot cast " + v.getClass().getName() + " to " + cast.type, cast.getLocation());
    }

    private org.objectweb.asm.Type getConstantValue2(ClassLiteral cl) {
        return org.objectweb.asm.Type.getType(getDescriptor(cl.type));
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

        if (v.endsWith("l")) {
            v = v.substring(0, v.length() - 1);
            return signed ? Long.parseLong(v, radix) : Long.parseUnsignedLong(v, radix);
        } else {
            return signed ? Integer.parseInt(v, radix) : Integer.parseUnsignedInt(v, radix);
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

    private String getClassSignature(TypeParameter[] typeParameters, ReferenceType superclass, ReferenceType[] interfaces) {
        if (checkNoClassSignature(typeParameters, superclass, interfaces)) {
            return null;
        }

        SignatureWriter sw = new SignatureWriter();
        visitTypeParameters(typeParameters, sw);

        if (superclass == null) {
            sw.visitSuperclass();
            sw.visitClassType(OBJECT);
            sw.visitEnd();
        } else {
            visitTypeSignature(superclass, sw.visitSuperclass());
        }

        for (ReferenceType inte : interfaces) {
            visitTypeSignature(inte, sw.visitInterface());
        }
        return sw.toString();
    }

    private boolean checkNoClassSignature(TypeParameter[] typeParameters, ReferenceType superclass, ReferenceType[] interfaces) {
        if (typeParameters != null && typeParameters.length != 0) return false;
        if (!checkNoSignature(superclass)) return false;
        for (ReferenceType inte : interfaces) {
            if (!checkNoSignature(inte)) return false;
        }
        return true;
    }

    private String getMethodSignature(TypeParameter[] typeParameters, Type[] parameterTypes, Type returnType, Type[] exceptionTypes) {
        if (checkNoMethodSignature(typeParameters, parameterTypes, returnType, exceptionTypes)) {
            return null;
        }

        SignatureWriter sw = new SignatureWriter();
        visitTypeParameters(typeParameters, sw);

        for (Type parameterType : parameterTypes) {
            visitTypeSignature(parameterType, sw.visitParameterType());
        }

        visitTypeSignature(returnType, sw.visitReturnType());

        for (Type exceptionType : exceptionTypes) {
            visitTypeSignature(exceptionType, sw.visitExceptionType());
        }

        return sw.toString();
    }

    private boolean checkNoMethodSignature(TypeParameter[] typeParameters, Type[] parameterTypes, Type returnType, Type[] exceptionTypes) {
        if (typeParameters != null && typeParameters.length != 0) return false;
        for (Type parameterType : parameterTypes) {
            if (!checkNoSignature(parameterType)) return false;
        }
        if (!checkNoSignature(returnType)) return false;
        for (Type exceptionType : exceptionTypes) {
            if (!checkNoSignature(exceptionType)) return false;
        }
        return true;
    }

    private String getTypeSignature(Type type) {
        if (checkNoSignature(type)) {
            return null;
        }
        SignatureWriter sw = new SignatureWriter();
        visitTypeSignature(type, sw);
        return sw.toString();
    }

    private boolean checkNoSignature(Type type) {
        if (type instanceof ReferenceType) {
            return ((ReferenceType) type).typeArguments.length == 0;
        } else if (type instanceof ArrayType) {
            return checkNoSignature(((ArrayType) type).componentType);
        } else {
            return true;
        }
    }

    private void visitTypeParameters(TypeParameter[] typeParameters, SignatureVisitor sw) {
        if (typeParameters == null) return;
        for (TypeParameter typeParameter : typeParameters) {
            visitTypeParameter(typeParameter, sw);
        }
    }

    private void visitTypeParameter(TypeParameter typeParameter, SignatureVisitor sw) {
        sw.visitFormalTypeParameter(typeParameter.name);
        ReferenceType[] bounds = typeParameter.bounds;
        if (bounds.length != 0) {
            if (bounds[0] != null) {
                visitTypeSignature(bounds[0], sw.visitClassBound());
            }
            for (int i = 1; i < bounds.length; i++) {
                visitTypeSignature(bounds[i], sw.visitInterfaceBound());
            }
        }
    }

    private void visitTypeSignature(Type type, SignatureVisitor sw) {
        if (type instanceof ArrayType) {
            ArrayType at = (ArrayType) type;
            visitTypeSignature(at.componentType, sw.visitArrayType());
        } else if (type instanceof ReferenceType) {
            ReferenceType rt = (ReferenceType) type;
            sw.visitClassType(getInternalName(rt.identifiers));
            for (TypeArgument typeArgument : rt.typeArguments) {
                if (typeArgument instanceof ReferenceType) {
                    visitTypeSignature((ReferenceType) typeArgument, sw.visitTypeArgument(SignatureVisitor.INSTANCEOF));
                } else if (typeArgument instanceof ArrayType) {
                    ArrayType at = (ArrayType) typeArgument;
                    visitTypeSignature(at.componentType, sw.visitArrayType());
                } else if (typeArgument instanceof Wildcard) {
                    Wildcard w = (Wildcard) typeArgument;
                    int bounds = w.bounds;
                    if (bounds == Wildcard.BOUNDS_NONE) {
                        sw.visitTypeArgument();
                    } else if (bounds == Wildcard.BOUNDS_EXTENDS) {
                        visitTypeSignature(w.type, sw.visitTypeArgument(SignatureVisitor.EXTENDS));
                    } else if (bounds == Wildcard.BOUNDS_SUPER) {
                        visitTypeSignature(w.type, sw.visitTypeArgument(SignatureVisitor.SUPER));
                    }
                }
            }
            sw.visitEnd();
        } else if (type instanceof PrimitiveType) {
            switch (((PrimitiveType) type).primitive) {
                case VOID:
                    sw.visitBaseType('V');
                    return;
                case BOOLEAN:
                    sw.visitBaseType('Z');
                    return;
                case CHAR:
                    sw.visitBaseType('C');
                    return;
                case BYTE:
                    sw.visitBaseType('B');
                    return;
                case SHORT:
                    sw.visitBaseType('S');
                    return;
                case INT:
                    sw.visitBaseType('I');
                    return;
                case FLOAT:
                    sw.visitBaseType('F');
                    return;
                case LONG:
                    sw.visitBaseType('J');
                    return;
                case DOUBLE:
                    sw.visitBaseType('D');
                    return;
                default:
                    throw new InternalCompileException("primitive");
            }
        }
    }

    private void push(boolean value, MethodVisitor mv) {
        push(value ? 1 : 0, mv);
    }

    private void push(int value, MethodVisitor mv) {
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

    private void push(long value, MethodVisitor mv) {
        if (value == 0L || value == 1L) {
            mv.visitInsn(Opcodes.LCONST_0 + (int) value);
        } else {
            mv.visitLdcInsn(value);
        }
    }

    private void push(float value, MethodVisitor mv) {
        int bits = Float.floatToIntBits(value);
        if (bits == 0L || bits == 0x3F800000 || bits == 0x40000000) { // 0..2
            mv.visitInsn(Opcodes.FCONST_0 + (int) value);
        } else {
            mv.visitLdcInsn(value);
        }
    }

    private void push(double value, MethodVisitor mv) {
        long bits = Double.doubleToLongBits(value);
        if (bits == 0L || bits == 0x3FF0000000000000L) { // +0.0d and 1.0d
            mv.visitInsn(Opcodes.DCONST_0 + (int) value);
        } else {
            mv.visitLdcInsn(value);
        }
    }

    private void push(String value, MethodVisitor mv) {
        if (value == null) {
            mv.visitInsn(Opcodes.ACONST_NULL);
        } else {
            mv.visitLdcInsn(value);
        }
    }

    private static final String CLASS_DESCRIPTOR = "Ljava/lang/Class;";

    private void push(org.objectweb.asm.Type value, MethodVisitor mv) {
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
