package com.github.mouse0w0.instantcoffee;

import com.github.mouse0w0.instantcoffee.model.*;
import com.github.mouse0w0.instantcoffee.model.ConstantDynamic;
import com.github.mouse0w0.instantcoffee.model.Handle;
import com.github.mouse0w0.instantcoffee.model.Type;
import com.github.mouse0w0.instantcoffee.model.statement.*;
import com.github.mouse0w0.instantcoffee.model.statement.Label;
import org.objectweb.asm.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static com.github.mouse0w0.instantcoffee.Constants.*;

public class Decompiler {
    private static final ModuleVisitor SKIP_MODULE_VISITOR = new ModuleVisitor(Opcodes.ASM9) {
    };
    private static final RecordComponentVisitor SKIP_RECORD_COMPONENT_VISITOR = new RecordComponentVisitor(Opcodes.ASM9) {
    };
    private static final AnnotationVisitor SKIP_ANNOTATION_VISITOR = new AnnotationVisitor(Opcodes.ASM9) {
    };

    public static ClassDeclaration decompile(ClassReader cr) {
        return new Decompiler().decompileClass(cr);
    }

    private boolean failOnUnsupportedFeature;

    public boolean isFailOnUnsupportedFeature() {
        return failOnUnsupportedFeature;
    }

    public void setFailOnUnsupportedFeature(boolean failOnUnsupportedFeature) {
        this.failOnUnsupportedFeature = failOnUnsupportedFeature;
    }

    public ClassDeclaration decompileClass(ClassReader cr) {
        MyClassVisitor cv = new MyClassVisitor();
        cr.accept(cv, ClassReader.SKIP_FRAMES);
        return cv.cd;
    }

    private static IntegerLiteral parseVersion(int version) {
        return new IntegerLiteral(Location.UNKNOWN, Integer.toString((version - 44) & 0xFFFF));
    }

    private static List<Modifier> parseClassModifiers(int access) {
        List<Modifier> l = new ArrayList<>();
        if ((access & ACC_PUBLIC) != 0)
            l.add(new Modifier(Location.UNKNOWN, "public"));
        else if ((access & ACC_PRIVATE) != 0)
            l.add(new Modifier(Location.UNKNOWN, "private"));
        else if ((access & ACC_PROTECTED) != 0)
            l.add(new Modifier(Location.UNKNOWN, "protected"));

        if ((access & ACC_SYNTHETIC) != 0)
            l.add(new Modifier(Location.UNKNOWN, "synthetic"));
        if ((access & ACC_STATIC) != 0)
            l.add(new Modifier(Location.UNKNOWN, "static"));
        if ((access & ACC_FINAL) != 0)
            l.add(new Modifier(Location.UNKNOWN, "final"));

        if ((access & ACC_RECORD) != 0) {
            l.add(new Modifier(Location.UNKNOWN, "record"));
        } else if ((access & ACC_ENUM) != 0) {
            l.add(new Modifier(Location.UNKNOWN, "enum"));
        } else if ((access & ACC_ANNOTATION) != 0) {
            l.add(new Modifier(Location.UNKNOWN, "@interface"));
        } else if ((access & ACC_INTERFACE) != 0) {
            l.add(new Modifier(Location.UNKNOWN, "interface"));
        } else {
            if ((access & ACC_ABSTRACT) != 0)
                l.add(new Modifier(Location.UNKNOWN, "abstract"));
            if ((access & ACC_SUPER) != 0)
                l.add(new Modifier(Location.UNKNOWN, "class"));
        }
        return l;
    }

    private static List<Modifier> parseFieldModifiers(int access) {
        List<Modifier> l = new ArrayList<>();
        if ((access & ACC_PUBLIC) != 0)
            l.add(new Modifier(Location.UNKNOWN, "public"));
        else if ((access & ACC_PRIVATE) != 0)
            l.add(new Modifier(Location.UNKNOWN, "private"));
        else if ((access & ACC_PROTECTED) != 0)
            l.add(new Modifier(Location.UNKNOWN, "protected"));

        if ((access & ACC_STATIC) != 0)
            l.add(new Modifier(Location.UNKNOWN, "static"));
        if ((access & ACC_FINAL) != 0)
            l.add(new Modifier(Location.UNKNOWN, "final"));
        if ((access & ACC_VOLATILE) != 0)
            l.add(new Modifier(Location.UNKNOWN, "volatile"));
        if ((access & ACC_TRANSIENT) != 0)
            l.add(new Modifier(Location.UNKNOWN, "transient"));
        if ((access & ACC_SYNTHETIC) != 0)
            l.add(new Modifier(Location.UNKNOWN, "synthetic"));
        if ((access & ACC_ENUM) != 0)
            l.add(new Modifier(Location.UNKNOWN, "enum"));
        if ((access & ACC_MANDATED) != 0)
            l.add(new Modifier(Location.UNKNOWN, "mandated"));
        return l;
    }

    private static List<Modifier> parseMethodModifiers(int access) {
        List<Modifier> l = new ArrayList<>();
        if ((access & ACC_PUBLIC) != 0)
            l.add(new Modifier(Location.UNKNOWN, "public"));
        else if ((access & ACC_PRIVATE) != 0)
            l.add(new Modifier(Location.UNKNOWN, "private"));
        else if ((access & ACC_PROTECTED) != 0)
            l.add(new Modifier(Location.UNKNOWN, "protected"));

        if ((access & ACC_SYNTHETIC) != 0)
            l.add(new Modifier(Location.UNKNOWN, "synthetic"));
        if ((access & ACC_STATIC) != 0)
            l.add(new Modifier(Location.UNKNOWN, "static"));
        if ((access & ACC_FINAL) != 0)
            l.add(new Modifier(Location.UNKNOWN, "final"));
        if ((access & ACC_SYNCHRONIZED) != 0)
            l.add(new Modifier(Location.UNKNOWN, "synchronized"));
        if ((access & ACC_BRIDGE) != 0)
            l.add(new Modifier(Location.UNKNOWN, "bridge"));
        if ((access & ACC_VARARGS) != 0)
            l.add(new Modifier(Location.UNKNOWN, "varargs"));
        if ((access & ACC_NATIVE) != 0)
            l.add(new Modifier(Location.UNKNOWN, "native"));
        if ((access & ACC_ABSTRACT) != 0)
            l.add(new Modifier(Location.UNKNOWN, "abstract"));
        if ((access & ACC_STRICT) != 0)
            l.add(new Modifier(Location.UNKNOWN, "strictfp"));
        if ((access & ACC_MANDATED) != 0)
            l.add(new Modifier(Location.UNKNOWN, "mandated"));
        return l;
    }

    private static String[] parseIdentifiers(String internalName) {
        return parseIdentifiers(internalName, 0, internalName.length());
    }

    private static String[] parseIdentifiers(String internalName, int begin, int end) {
        List<String> list = new ArrayList<>();
        int i = begin;
        while (i < end) {
            int slash = internalName.indexOf('/', i);
            if (slash == -1 || slash >= end) {
                list.add(internalName.substring(i, end));
                break;
            }
            list.add(internalName.substring(i, slash));
            i = slash + 1;
        }
        return list.toArray(EMPTY_STRING_ARRAY);
    }

    private static Type parseArrayOrInternal(String internalName) {
        return parseArrayOrInternal(internalName, 0, internalName.length());
    }

    private static Type parseArrayOrInternal(String internalName, int begin, int end) {
        if (internalName.charAt(begin) == '[') {
            return parseType(internalName, begin, end);
        } else {
            return parseInternal(internalName, begin, end);
        }
    }

    private static ReferenceType parseInternal(String internalName) {
        return parseInternal(internalName, 0, internalName.length());
    }

    private static ReferenceType parseInternal(String internalName, int begin, int end) {
        return new ReferenceType(Location.UNKNOWN, parseIdentifiers(internalName, begin, end));
    }

    private static Type parseType(String typeDescriptor) {
        return parseType(typeDescriptor, 0, typeDescriptor.length());
    }

    private static Type parseType(String typeDescriptor, int begin, int end) {
        switch (typeDescriptor.charAt(begin)) {
            case 'V':
                return new VoidType(Location.UNKNOWN);
            case 'Z':
                return new PrimitiveType(Location.UNKNOWN, Primitive.BOOLEAN);
            case 'C':
                return new PrimitiveType(Location.UNKNOWN, Primitive.CHAR);
            case 'B':
                return new PrimitiveType(Location.UNKNOWN, Primitive.BYTE);
            case 'S':
                return new PrimitiveType(Location.UNKNOWN, Primitive.SHORT);
            case 'I':
                return new PrimitiveType(Location.UNKNOWN, Primitive.INT);
            case 'F':
                return new PrimitiveType(Location.UNKNOWN, Primitive.FLOAT);
            case 'J':
                return new PrimitiveType(Location.UNKNOWN, Primitive.LONG);
            case 'D':
                return new PrimitiveType(Location.UNKNOWN, Primitive.DOUBLE);
            case '[':
                return new ArrayType(Location.UNKNOWN, parseType(typeDescriptor, begin + 1, end));
            case 'L':
                return parseInternal(typeDescriptor, begin + 1, end - 1);
            case '(':
                throw new IllegalArgumentException("method descriptor");
            default:
                throw new IllegalArgumentException();
        }
    }

    private static List<Type> parseParameterTypes(String methodDescriptor) {
        List<Type> parameterTypes = new ArrayList<>();
        int currentOffset = 1;
        while (methodDescriptor.charAt(currentOffset) != ')') {
            int currentParameterTypeOffset = currentOffset;
            while (methodDescriptor.charAt(currentOffset) == '[') {
                currentOffset++;
            }
            if (methodDescriptor.charAt(currentOffset++) == 'L') {
                int semiOffset = methodDescriptor.indexOf(';', currentOffset);
                if (semiOffset != -1) currentOffset = semiOffset + 1;
            }
            parameterTypes.add(parseType(methodDescriptor, currentParameterTypeOffset, currentOffset));
        }
        return parameterTypes;
    }

    private static Type parseReturnType(String methodDescriptor) {
        return parseType(methodDescriptor, methodDescriptor.indexOf(')') + 1, methodDescriptor.length());
    }

    private static PrimitiveType parseNewArrayInsnType(int operand) {
        switch (operand) {
            case T_BOOLEAN:
                return new PrimitiveType(Location.UNKNOWN, Primitive.BOOLEAN);
            case T_CHAR:
                return new PrimitiveType(Location.UNKNOWN, Primitive.CHAR);
            case T_FLOAT:
                return new PrimitiveType(Location.UNKNOWN, Primitive.FLOAT);
            case T_DOUBLE:
                return new PrimitiveType(Location.UNKNOWN, Primitive.DOUBLE);
            case T_BYTE:
                return new PrimitiveType(Location.UNKNOWN, Primitive.BYTE);
            case T_SHORT:
                return new PrimitiveType(Location.UNKNOWN, Primitive.SHORT);
            case T_INT:
                return new PrimitiveType(Location.UNKNOWN, Primitive.INT);
            case T_LONG:
                return new PrimitiveType(Location.UNKNOWN, Primitive.LONG);
            default:
                throw new IllegalArgumentException("Unsupported newarray operand: " + operand);
        }
    }

    private static Value parseValue(Object value) {
        if (value == null) {
            return new NullLiteral(Location.UNKNOWN);
        } else if (value instanceof String) {
            return new StringLiteral(Location.UNKNOWN, "\"" + escape(value.toString()) + "\"");
        } else if (value instanceof Byte) {
            return new IntegerLiteral(Location.UNKNOWN, value + "B");
        } else if (value instanceof Short) {
            return new IntegerLiteral(Location.UNKNOWN, value + "S");
        } else if (value instanceof Integer) {
            return new IntegerLiteral(Location.UNKNOWN, value.toString());
        } else if (value instanceof Long) {
            return new IntegerLiteral(Location.UNKNOWN, value + "L");
        } else if (value instanceof Float) {
            return new FloatingPointLiteral(Location.UNKNOWN, value + "F");
        } else if (value instanceof Double) {
            return new FloatingPointLiteral(Location.UNKNOWN, value + "D");
        } else if (value instanceof Boolean) {
            return new BooleanLiteral(Location.UNKNOWN, value.toString());
        } else if (value instanceof Character) {
            return new CharacterLiteral(Location.UNKNOWN, "'" + escape(value.toString()) + "'");
        } else if (value instanceof org.objectweb.asm.Type) {
            String descriptor = value.toString();
            if (descriptor.charAt(0) == '(') {
                return parseMethodType(descriptor);
            } else {
                return parseType(descriptor);
            }
        } else if (value instanceof org.objectweb.asm.Handle) {
            return parseHandle((org.objectweb.asm.Handle) value);
        } else if (value instanceof org.objectweb.asm.ConstantDynamic) {
            return parseConstantDynamic((org.objectweb.asm.ConstantDynamic) value);
        } else {
            throw new IllegalArgumentException("Unsupported type: " + value.getClass());
        }
    }

    private static AnnotationValue parseAnnotationValue(Object value) {
        if (value instanceof String) {
            return new StringLiteral(Location.UNKNOWN, "\"" + escape(value.toString()) + "\"");
        } else if (value instanceof Byte) {
            return new IntegerLiteral(Location.UNKNOWN, value + "B");
        } else if (value instanceof Short) {
            return new IntegerLiteral(Location.UNKNOWN, value + "S");
        } else if (value instanceof Integer) {
            return new IntegerLiteral(Location.UNKNOWN, value.toString());
        } else if (value instanceof Long) {
            return new IntegerLiteral(Location.UNKNOWN, value + "L");
        } else if (value instanceof Float) {
            return new FloatingPointLiteral(Location.UNKNOWN, value + "F");
        } else if (value instanceof Double) {
            return new FloatingPointLiteral(Location.UNKNOWN, value + "D");
        } else if (value instanceof Boolean) {
            return new BooleanLiteral(Location.UNKNOWN, value.toString());
        } else if (value instanceof Character) {
            return new CharacterLiteral(Location.UNKNOWN, "'" + escape(value.toString()) + "'");
        } else if (value instanceof org.objectweb.asm.Type) {
            String descriptor = value.toString();
            if (descriptor.charAt(0) == '(') {
                throw new IllegalArgumentException("Annotation value cannot be a method descriptor: " + descriptor);
            } else {
                return parseType(descriptor);
            }
        } else if (value == null) {
            throw new NullPointerException("Annotation value cannot be null");
        } else {
            throw new IllegalArgumentException("Annotation value cannot be of type: " + value.getClass());
        }
    }

    private static MethodType parseMethodType(String methodDescriptor) {
        List<Type> parameterTypes = parseParameterTypes(methodDescriptor);
        Type returnType = parseReturnType(methodDescriptor);
        return new MethodType(Location.UNKNOWN, parameterTypes, returnType);
    }

    private static Handle parseHandle(org.objectweb.asm.Handle handle) {
        String kind = getHandleKindName(handle.getTag() | (handle.isInterface() && handle.getTag() != H_INVOKEINTERFACE ? FLAG_INTERFACE : 0));
        ReferenceType owner = parseInternal(handle.getOwner());
        String name = handle.getName();
        if (handle.getTag() < H_INVOKEVIRTUAL) {
            return new Handle(Location.UNKNOWN, kind, owner, name, parseType(handle.getDesc()));
        } else {
            return new Handle(Location.UNKNOWN, kind, owner, name, parseMethodType(handle.getDesc()));
        }
    }

    private static ConstantDynamic parseConstantDynamic(org.objectweb.asm.ConstantDynamic constantDynamic) {
        String name = constantDynamic.getName();
        Type type = parseType(constantDynamic.getDescriptor());
        Handle bootstrapMethod = parseHandle(constantDynamic.getBootstrapMethod());
        int bootstrapMethodArgumentCount = constantDynamic.getBootstrapMethodArgumentCount();
        List<Value> bootstrapMethodArguments = new ArrayList<>(bootstrapMethodArgumentCount);
        for (int i = 0; i < bootstrapMethodArgumentCount; i++) {
            bootstrapMethodArguments.add(parseValue(constantDynamic.getBootstrapMethodArgument(i)));
        }
        return new ConstantDynamic(Location.UNKNOWN, name, type, bootstrapMethod, bootstrapMethodArguments);
    }

    private static IntegerLiteral parseIntegerLiteral(int value) {
        return new IntegerLiteral(Location.UNKNOWN, Integer.toString(value));
    }

    private static String escape(String s) {
        StringBuilder sb = new StringBuilder(s.length());

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            int idx = "\b\t\n\f\r\"'\\".indexOf(c);
            if (idx != -1) {
                sb.append("\\").append("btnfr\"'\\".charAt(idx));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private static EnumLiteral parseEnumLiteral(String descriptor, String value) {
        return new EnumLiteral(Location.UNKNOWN, parseInternal(descriptor, 1, descriptor.length() - 1), value);
    }

    private class MyClassVisitor extends ClassVisitor {
        private final ClassDeclaration cd = new ClassDeclaration(Location.UNKNOWN);

        public MyClassVisitor() {
            super(Opcodes.ASM9);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            cd.version = parseVersion(version);
            cd.modifiers = parseClassModifiers(access);
            cd.identifiers = parseIdentifiers(name);
            cd.superclass = "java/lang/Object".equals(superName) ? null : parseInternal(superName);
            for (String inte : interfaces) {
                cd.interfaces.add(parseInternal(inte));
            }
        }

        @Override
        public void visitSource(String source, String debug) {
            if (source != null) {
                cd.source = new StringLiteral(Location.UNKNOWN, "\"" + escape(source) + "\"");
            }
        }

        @Override
        public ModuleVisitor visitModule(String name, int access, String version) {
            if (isFailOnUnsupportedFeature()) {
                throw new UnsupportedOperationException("module");
            }
            return SKIP_MODULE_VISITOR;
        }

        @Override
        public void visitNestHost(String nestHost) {
            if (nestHost != null) {
                cd.nestHost = parseInternal(nestHost);
            }
        }

        @Override
        public void visitOuterClass(String owner, String name, String descriptor) {
            if (isFailOnUnsupportedFeature()) {
                throw new UnsupportedOperationException("outer class");
            }
        }

        @Override
        public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
            Annotation annotation = new Annotation(Location.UNKNOWN);
            annotation.type = parseInternal(descriptor, 1, descriptor.length() - 1);
            annotation.visible = visible;
            cd.annotations.add(annotation);
            return new NormalAnnotationVisitor(annotation);
        }

        @Override
        public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
            if (isFailOnUnsupportedFeature()) {
                throw new UnsupportedOperationException("type annotation");
            }
            return SKIP_ANNOTATION_VISITOR;
        }

        @Override
        public void visitAttribute(Attribute attribute) {
            if (isFailOnUnsupportedFeature()) {
                throw new UnsupportedOperationException("attribute");
            }
        }

        @Override
        public void visitNestMember(String nestMember) {
            cd.nestMembers.add(parseInternal(nestMember));
        }

        @Override
        public void visitPermittedSubclass(String permittedSubclass) {
            if (isFailOnUnsupportedFeature()) {
                throw new UnsupportedOperationException("permitted subclass");
            }
        }

        @Override
        public void visitInnerClass(String name, String outerName, String innerName, int access) {
            InnerClassType type;

            if (innerName == null) {
                type = InnerClassType.ANONYMOUS;
            } else if (outerName == null) {
                type = InnerClassType.LOCAL;
            } else {
                type = InnerClassType.MEMBER_OR_STATIC;
            }

            cd.innerClasses.add(new InnerClassDeclaration(Location.UNKNOWN, type, parseClassModifiers(access),
                    parseIdentifiers(name), innerName));
        }

        @Override
        public RecordComponentVisitor visitRecordComponent(String name, String descriptor, String signature) {
            if (isFailOnUnsupportedFeature()) {
                throw new UnsupportedOperationException("record component");
            }
            return SKIP_RECORD_COMPONENT_VISITOR;
        }

        @Override
        public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
            FieldDeclaration fd = new FieldDeclaration(Location.UNKNOWN);
            fd.modifiers = parseFieldModifiers(access);
            fd.type = parseType(descriptor);
            fd.name = name;
            fd.value = parseValue(value);
            cd.fields.add(fd);
            return new MyFieldVisitor(fd);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            MethodDeclaration md = new MethodDeclaration(Location.UNKNOWN);
            md.modifiers = parseMethodModifiers(access);
            md.name = name;
            md.parameterTypes = parseParameterTypes(descriptor);
            md.returnType = parseReturnType(descriptor);
            if (exceptions != null) {
                for (String exception : exceptions) {
                    md.exceptionTypes.add(parseInternal(exception));
                }
            }
            cd.methods.add(md);
            return new MyMethodVisitor(md);
        }

        @Override
        public void visitEnd() {
            // Nothing to do.
        }
    }

    private class MyFieldVisitor extends FieldVisitor {
        private final FieldDeclaration fd;

        public MyFieldVisitor(FieldDeclaration fd) {
            super(Opcodes.ASM9);
            this.fd = fd;
        }

        @Override
        public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
            Annotation annotation = new Annotation(Location.UNKNOWN);
            annotation.type = parseInternal(descriptor, 1, descriptor.length() - 1);
            annotation.visible = visible;
            fd.annotations.add(annotation);
            return new NormalAnnotationVisitor(annotation);
        }

        @Override
        public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
            if (isFailOnUnsupportedFeature()) {
                throw new UnsupportedOperationException("type annotation");
            }
            return SKIP_ANNOTATION_VISITOR;
        }

        @Override
        public void visitAttribute(Attribute attribute) {
            if (isFailOnUnsupportedFeature()) {
                throw new UnsupportedOperationException("attribute");
            }
        }

        @Override
        public void visitEnd() {
            // Nothing to do.
        }
    }

    private class MyMethodVisitor extends MethodVisitor {
        private final MethodDeclaration md;
        private final List<Statement> statements = new ArrayList<>();

        private final Map<org.objectweb.asm.Label, Label> labelMap = new HashMap<>();

        public MyMethodVisitor(MethodDeclaration md) {
            super(Opcodes.ASM9);
            this.md = md;
        }

        @Override
        public void visitParameter(String name, int access) {
            if (isFailOnUnsupportedFeature()) {
                throw new UnsupportedOperationException("parameter");
            }
        }

        @Override
        public AnnotationVisitor visitAnnotationDefault() {
            return new ValueAnnotationVisitor(value -> md.defaultValue = value);
        }

        @Override
        public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
            Annotation annotation = new Annotation(Location.UNKNOWN);
            annotation.type = parseInternal(descriptor, 1, descriptor.length() - 1);
            annotation.visible = visible;
            md.annotations.add(annotation);
            return new NormalAnnotationVisitor(annotation);
        }

        @Override
        public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
            if (isFailOnUnsupportedFeature()) {
                throw new UnsupportedOperationException("type annotation");
            }
            return SKIP_ANNOTATION_VISITOR;
        }

        @Override
        public void visitAnnotableParameterCount(int parameterCount, boolean visible) {
            if (isFailOnUnsupportedFeature()) {
                throw new UnsupportedOperationException("parameter annotation count");
            }
        }

        @Override
        public AnnotationVisitor visitParameterAnnotation(int parameter, String descriptor, boolean visible) {
            if (isFailOnUnsupportedFeature()) {
                throw new UnsupportedOperationException("parameter annotation");
            }
            return SKIP_ANNOTATION_VISITOR;
        }

        @Override
        public void visitAttribute(Attribute attribute) {
            if (isFailOnUnsupportedFeature()) {
                throw new UnsupportedOperationException("attribute");
            }
        }

        @Override
        public void visitCode() {
            // Nothing to do.
        }

        @Override
        public void visitFrame(int type, int numLocal, Object[] local, int numStack, Object[] stack) {
            if (isFailOnUnsupportedFeature()) {
                throw new UnsupportedOperationException("frame");
            }
        }

        @Override
        public void visitInsn(int opcode) {
            statements.add(new Insn(Location.UNKNOWN, getOpcodeName(opcode)));
        }

        @Override
        public void visitIntInsn(int opcode, int operand) {
            if (opcode == NEWARRAY) {
                statements.add(new NewArrayInsn(Location.UNKNOWN, parseNewArrayInsnType(operand)));
            } else {
                statements.add(new IntInsn(Location.UNKNOWN, getOpcodeName(opcode), parseIntegerLiteral(operand)));
            }
        }

        @Override
        public void visitVarInsn(int opcode, int var) {
            statements.add(new VarInsn(Location.UNKNOWN, getOpcodeName(opcode), parseIntegerLiteral(var)));
        }

        @Override
        public void visitTypeInsn(int opcode, String type) {
            statements.add(new TypeInsn(Location.UNKNOWN, getOpcodeName(opcode), parseArrayOrInternal(type)));
        }

        @Override
        public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
            statements.add(new FieldInsn(Location.UNKNOWN, getOpcodeName(opcode), parseInternal(owner), name, parseType(descriptor)));
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
            String opcodeName = getOpcodeName(opcode | (isInterface ? FLAG_INTERFACE : 0));
            List<Type> parameterTypes = parseParameterTypes(descriptor);
            Type returnType = parseReturnType(descriptor);
            statements.add(new MethodInsn(
                    Location.UNKNOWN,
                    opcodeName,
                    parseArrayOrInternal(owner),
                    name,
                    new MethodType(Location.UNKNOWN, parameterTypes, returnType)
            ));
        }

        @Override
        public void visitInvokeDynamicInsn(String name, String descriptor, org.objectweb.asm.Handle bootstrapMethod, Object... bootstrapMethodArguments) {
            MethodType methodType = parseMethodType(descriptor);
            Handle newBootstrapMethod = parseHandle(bootstrapMethod);
            List<Value> newBootstrapMethodArguments = new ArrayList<>(bootstrapMethodArguments.length);
            for (Object bootstrapMethodArgument : bootstrapMethodArguments) {
                newBootstrapMethodArguments.add(parseValue(bootstrapMethodArgument));
            }
            statements.add(new InvokeDynamicInsn(
                    Location.UNKNOWN,
                    name,
                    methodType,
                    newBootstrapMethod,
                    newBootstrapMethodArguments
            ));
        }

        @Override
        public void visitJumpInsn(int opcode, org.objectweb.asm.Label label) {
            statements.add(new JumpInsn(Location.UNKNOWN, getOpcodeName(opcode), getLabel(label).name));
        }

        @Override
        public void visitLabel(org.objectweb.asm.Label label) {
            statements.add(getLabel(label));
        }

        private Label getLabel(org.objectweb.asm.Label label) {
            Label labelInsn = labelMap.get(label);
            if (labelInsn == null) {
                labelInsn = new Label(Location.UNKNOWN, getLabelName(labelMap.size() + 1));
                labelMap.put(label, labelInsn);
            }
            return labelInsn;
        }

        private String getLabelName(int idx) {
            char[] buf = new char[7];
            int charPos = 7;

            while (idx > 0) {
                buf[--charPos] = (char) ('A' + ((idx - 1) % 26));
                idx = (idx - 1) / 26;
            }

            return new String(buf, charPos, 7 - charPos);
        }

        @Override
        public void visitLdcInsn(Object value) {
            statements.add(new LdcInsn(Location.UNKNOWN, parseValue(value)));
        }

        @Override
        public void visitIincInsn(int var, int increment) {
            statements.add(new IincInsn(
                    Location.UNKNOWN,
                    parseIntegerLiteral(var),
                    parseIntegerLiteral(increment)));
        }

        @Override
        public void visitTableSwitchInsn(int min, int max, org.objectweb.asm.Label dflt, org.objectweb.asm.Label... labels) {
            List<SwitchCase> insnCases = new ArrayList<>(labels.length);
            for (int i = 0; i < labels.length; i++) {
                insnCases.add(new SwitchCase(Location.UNKNOWN, parseIntegerLiteral(i + min), getLabel(labels[i]).name));
            }
            String insnDflt = getLabel(dflt).name;
            statements.add(new SwitchInsn(
                    Location.UNKNOWN,
                    insnCases,
                    insnDflt));
        }

        @Override
        public void visitLookupSwitchInsn(org.objectweb.asm.Label dflt, int[] keys, org.objectweb.asm.Label[] labels) {
            List<SwitchCase> insnCases = new ArrayList<>(labels.length);
            for (int i = 0; i < labels.length; i++) {
                insnCases.add(new SwitchCase(Location.UNKNOWN, parseIntegerLiteral(keys[i]), getLabel(labels[i]).name));
            }
            String insnDflt = getLabel(dflt).name;
            statements.add(new SwitchInsn(Location.UNKNOWN, insnCases, insnDflt));
        }

        @Override
        public void visitMultiANewArrayInsn(String descriptor, int numDimensions) {
            statements.add(new MultiANewArrayInsn(
                    Location.UNKNOWN,
                    parseType(descriptor),
                    parseIntegerLiteral(numDimensions)));
        }

        @Override
        public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
            if (isFailOnUnsupportedFeature()) {
                throw new UnsupportedOperationException("insn annotation");
            }
            return SKIP_ANNOTATION_VISITOR;
        }

        @Override
        public void visitTryCatchBlock(org.objectweb.asm.Label start, org.objectweb.asm.Label end, org.objectweb.asm.Label handler, String type) {
            statements.add(new TryCatchBlock(
                    Location.UNKNOWN,
                    getLabel(start).name,
                    getLabel(end).name,
                    getLabel(handler).name,
                    type != null ? parseInternal(type) : null
            ));
        }

        @Override
        public AnnotationVisitor visitTryCatchAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
            if (isFailOnUnsupportedFeature()) {
                throw new UnsupportedOperationException("try catch annotation");
            }
            return SKIP_ANNOTATION_VISITOR;
        }

        @Override
        public void visitLocalVariable(String name, String descriptor, String signature, org.objectweb.asm.Label start, org.objectweb.asm.Label end, int index) {
            statements.add(new LocalVariable(
                    Location.UNKNOWN,
                    name,
                    parseType(descriptor),
                    getLabel(start).name,
                    getLabel(end).name,
                    parseIntegerLiteral(index)));
        }

        @Override
        public AnnotationVisitor visitLocalVariableAnnotation(int typeRef, TypePath typePath, org.objectweb.asm.Label[] start, org.objectweb.asm.Label[] end, int[] index, String descriptor, boolean visible) {
            if (isFailOnUnsupportedFeature()) {
                throw new UnsupportedOperationException("local variable annotation");
            }
            return SKIP_ANNOTATION_VISITOR;
        }

        @Override
        public void visitLineNumber(int line, org.objectweb.asm.Label start) {
            statements.add(new LineNumber(
                    Location.UNKNOWN,
                    parseIntegerLiteral(line),
                    getLabel(start).name));
        }

        @Override
        public void visitMaxs(int maxStack, int maxLocals) {
            // Nothing to do.
        }

        @Override
        public void visitEnd() {
            md.body = new Block(Location.UNKNOWN, statements);
        }
    }

    private static class NormalAnnotationVisitor extends AnnotationVisitor {
        private final Annotation annotation;

        public NormalAnnotationVisitor(Annotation annotation) {
            super(Opcodes.ASM9);
            this.annotation = annotation;
        }

        private void addValue(String name, AnnotationValue value) {
            annotation.pairs.add(new AnnotationValuePair(Location.UNKNOWN, name, value));
        }

        @Override
        public void visit(String name, Object value) {
            addValue(name, parseAnnotationValue(value));
        }

        @Override
        public void visitEnum(String name, String descriptor, String value) {
            addValue(name, parseEnumLiteral(descriptor, value));
        }

        @Override
        public AnnotationVisitor visitAnnotation(String name, String descriptor) {
            Annotation annotation = new Annotation(Location.UNKNOWN);
            annotation.type = parseInternal(descriptor, 1, descriptor.length() - 1);
            addValue(name, annotation);
            return new NormalAnnotationVisitor(annotation);
        }

        @Override
        public AnnotationVisitor visitArray(String name) {
            return new ArrayAnnotationVisitor(arrayValue -> addValue(name, arrayValue));
        }

        @Override
        public void visitEnd() {
            // Nothing to do.
        }
    }

    private static class ValueAnnotationVisitor extends AnnotationVisitor {
        private final Consumer<AnnotationValue> callback;

        private AnnotationValue annotationValue;

        public ValueAnnotationVisitor(Consumer<AnnotationValue> callback) {
            super(Opcodes.ASM9);
            this.callback = callback;
        }

        @Override
        public void visit(String name, Object value) {
            annotationValue = parseAnnotationValue(value);
        }

        @Override
        public void visitEnum(String name, String descriptor, String value) {
            annotationValue = parseEnumLiteral(descriptor, value);
        }

        @Override
        public AnnotationVisitor visitAnnotation(String name, String descriptor) {
            Annotation annotation = new Annotation(Location.UNKNOWN);
            annotation.type = parseInternal(descriptor, 1, descriptor.length() - 1);
            annotationValue = annotation;
            return new NormalAnnotationVisitor(annotation);
        }

        @Override
        public AnnotationVisitor visitArray(String name) {
            return new ArrayAnnotationVisitor(arrayValue -> annotationValue = arrayValue);
        }

        @Override
        public void visitEnd() {
            callback.accept(annotationValue);
        }
    }

    private static class ArrayAnnotationVisitor extends AnnotationVisitor {
        private final Consumer<AnnotationValueArrayInitializer> callback;

        private final List<AnnotationValue> values = new ArrayList<>();

        public ArrayAnnotationVisitor(Consumer<AnnotationValueArrayInitializer> callback) {
            super(Opcodes.ASM9);
            this.callback = callback;
        }

        @Override
        public void visit(String name, Object value) {
            values.add(parseAnnotationValue(value));
        }

        @Override
        public void visitEnum(String name, String descriptor, String value) {
            values.add(parseEnumLiteral(descriptor, value));
        }

        @Override
        public AnnotationVisitor visitAnnotation(String name, String descriptor) {
            Annotation annotation = new Annotation(Location.UNKNOWN);
            annotation.type = parseInternal(descriptor, 1, descriptor.length() - 1);
            values.add(annotation);
            return new NormalAnnotationVisitor(annotation);
        }

        @Override
        public AnnotationVisitor visitArray(String name) {
            return new ArrayAnnotationVisitor(values::add);
        }

        @Override
        public void visitEnd() {
            callback.accept(new AnnotationValueArrayInitializer(Location.UNKNOWN, values));
        }
    }
}
