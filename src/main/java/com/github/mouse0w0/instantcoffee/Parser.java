package com.github.mouse0w0.instantcoffee;

import com.github.mouse0w0.instantcoffee.model.*;
import com.github.mouse0w0.instantcoffee.model.insn.*;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    private static final String[] MODIFIERS = {
            "public", // 0x0001
            "private", // 0x0002
            "protected", // 0x0004
            "static", // 0x0008
            "final", // 0x0010
            "class", // 0x0020
            "synchronized", // 0x0020
            "volatile", // 0x0040
            "bridge", // 0x0040
            "varargs", // 0x0080
            "transient", // 0x0080
            "native", // 0x0100
            "interface", // 0x0200
            "abstract", // 0x0400
            "strict", // 0x0800
            "synthetic", // 0x1000
            // "@interface", // 0x2000
            "enum", // 0x4000
            "mandated", // 0x8000
            "module", // 0x8000
            "record", // 0x10000
            "deprecated", // 0x20000
    };
    private static final String[] PRIMITIVES = {
            "boolean", "char", "byte", "short", "int", "float", "long", "double",
    };
    private static final TokenType[] LITERALS = {
            TokenType.NULL_LITERAL, TokenType.BOOLEAN_LITERAL, TokenType.CHARACTER_LITERAL,
            TokenType.INTEGER_LITERAL, TokenType.FLOATING_POINT_LITERAL, TokenType.STRING_LITERAL
    };

    private final TokenStream tokenStream;

    public Parser(TokenStream tokenStream) {
        this.tokenStream = tokenStream;
    }

    public ClassDeclaration parseClassDeclaration() {
        ClassDeclaration classDeclaration = new ClassDeclaration(
                location(),
                parseAnnotations(),
                parseModifiers(),
                parseQualifiedIdentifier(),
                parseSuperclass(),
                parseInterfaces());

        parseClassBody(classDeclaration);

        return classDeclaration;
    }

    private void parseClassBody(ClassDeclaration cd) {
        read("{");
        while (!peekRead("}")) {
            parseClassMember(cd);
        }
    }

    private void parseClassMember(ClassDeclaration cd) {
        if (peekRead("version")) {
            cd.version = parseIntegerLiteral();
            return;
        }

        if (peek("source")) {
            cd.source = parseSourceDeclaration();
            return;
        }

        Annotation[] annotations = parseAnnotations();
        Modifier[] modifiers = parseModifiers();
        if (peek("innerclass")) {
            if (annotations.length != 0) {
                throw new CompileException("Inner class cannot be annotated", location());
            }
            cd.innerClasses.add(parseInnerClassDeclaration(modifiers));
            return;
        }

        if (peek("{") && modifiers.length == 1 && "static".equals(modifiers[0].keyword)) {
            cd.methods.add(parseInitializerDeclaration(location(), annotations, modifiers));
            return;
        }

        if (peekRead("<")) {
            read("init");
            read(">");
            Location location = location();
            Type returnType = new PrimitiveType(location, Primitive.VOID);
            cd.methods.add(parseMethodDeclaration(
                    location,
                    annotations,
                    modifiers,
                    returnType,
                    "<init>",
                    false));
            return;
        }

        if (peek("void")) {
            Type returnType = parseVoid();
            Location location = location();
            String name = parseIdentifier();
            cd.methods.add(parseMethodDeclaration(
                    location,
                    annotations,
                    modifiers,
                    returnType,
                    name,
                    false));
            return;
        }

        Type returnType = parseType();
        Location location = location();
        String name = parseIdentifier();
        if (peek("(")) {
            cd.methods.add(parseMethodDeclaration(
                    location,
                    annotations,
                    modifiers,
                    returnType,
                    name,
                    hasModifier(cd.modifiers, "@interface")));
            return;
        }

        cd.fields.add(parseFieldDeclaration(location, annotations, modifiers, returnType, name));
    }

    private SourceDeclaration parseSourceDeclaration() {
        Location location = location();
        read("source");
        return new SourceDeclaration(location, parseStringLiteral());
    }

    private InnerClassDeclaration parseInnerClassDeclaration(Modifier[] modifiers) {
        Location location = location();
        read("innerclass");
        return new InnerClassDeclaration(location, modifiers, parseQualifiedIdentifier(), parseIdentifier());
    }

    private FieldDeclaration parseFieldDeclaration(Location location, Annotation[] annotations, Modifier[] modifiers, Type type, String name) {
        return new FieldDeclaration(location, annotations, modifiers, type, name, peekRead("=") ? parseValue() : null);
    }

    private MethodDeclaration parseInitializerDeclaration(Location location, Annotation[] annotations, Modifier[] modifiers) {
        MethodDeclaration methodDeclaration = new MethodDeclaration(
                location,
                annotations,
                modifiers,
                new PrimitiveType(location, Primitive.VOID),
                "<clinit>",
                Type.EMPTY_ARRAY,
                ReferenceType.EMPTY_ARRAY,
                null);
        parseMethodBody(methodDeclaration);
        return methodDeclaration;
    }

    private MethodDeclaration parseMethodDeclaration(Location location, Annotation[] annotations, Modifier[] modifiers, Type returnType, String name, boolean allowDefaultClause) {
        MethodDeclaration methodDeclaration = new MethodDeclaration(
                location,
                annotations,
                modifiers,
                returnType,
                name,
                parseMethodParameterTypes(),
                parseMethodExceptionTypes(),
                allowDefaultClause && peekRead("default") ? parseAnnotationValue() : null);
        parseMethodBody(methodDeclaration);
        return methodDeclaration;
    }

    private Type[] parseMethodParameterTypes() {
        read("(");
        if (peekRead(")")) {
            return Type.EMPTY_ARRAY;
        }
        List<Type> parameterTypes = new ArrayList<>();
        do {
            parameterTypes.add(parseType());
        } while (peekRead(","));
        read(")");
        return parameterTypes.toArray(Type.EMPTY_ARRAY);
    }

    private ReferenceType[] parseMethodExceptionTypes() {
        List<ReferenceType> exceptionTypes = new ArrayList<>();
        if (peekRead("throws")) {
            do {
                exceptionTypes.add(parseReferenceType());
            } while (peekRead(","));
        }
        return exceptionTypes.toArray(ReferenceType.EMPTY_ARRAY);
    }

    private void parseMethodBody(MethodDeclaration md) {
        boolean isAbstractOrNative = hasModifier(md.modifiers, "abstract", "native");
        if (!peek("{")) {
            if (isAbstractOrNative) {
                return;
            }
            throw new CompileException("Method must have a body", location());
        }
        if (isAbstractOrNative) {
            throw new CompileException("Abstract or native method must not have a body", location());
        }

        read();
        while (!peekRead("}")) {
            Location location = location();
            String insn = parseIdentifier();

            if (peekRead(":")) {
                md.instructions.add(new LabelInsn(location, insn));
                continue;
            }

            switch (Constants.getOpcode(insn) & 0xFF) {
                case Constants.NOP:
                case Constants.ACONST_NULL:
                case Constants.ICONST_M1:
                case Constants.ICONST_0:
                case Constants.ICONST_1:
                case Constants.ICONST_2:
                case Constants.ICONST_3:
                case Constants.ICONST_4:
                case Constants.ICONST_5:
                case Constants.LCONST_0:
                case Constants.LCONST_1:
                case Constants.FCONST_0:
                case Constants.FCONST_1:
                case Constants.FCONST_2:
                case Constants.DCONST_0:
                case Constants.DCONST_1:
                case Constants.IALOAD:
                case Constants.LALOAD:
                case Constants.FALOAD:
                case Constants.DALOAD:
                case Constants.AALOAD:
                case Constants.BALOAD:
                case Constants.CALOAD:
                case Constants.SALOAD:
                case Constants.IASTORE:
                case Constants.LASTORE:
                case Constants.FASTORE:
                case Constants.DASTORE:
                case Constants.AASTORE:
                case Constants.BASTORE:
                case Constants.CASTORE:
                case Constants.SASTORE:
                case Constants.POP:
                case Constants.POP2:
                case Constants.DUP:
                case Constants.DUP_X1:
                case Constants.DUP_X2:
                case Constants.DUP2:
                case Constants.DUP2_X1:
                case Constants.DUP2_X2:
                case Constants.SWAP:
                case Constants.IADD:
                case Constants.LADD:
                case Constants.FADD:
                case Constants.DADD:
                case Constants.ISUB:
                case Constants.LSUB:
                case Constants.FSUB:
                case Constants.DSUB:
                case Constants.IMUL:
                case Constants.LMUL:
                case Constants.FMUL:
                case Constants.DMUL:
                case Constants.IDIV:
                case Constants.LDIV:
                case Constants.FDIV:
                case Constants.DDIV:
                case Constants.IREM:
                case Constants.LREM:
                case Constants.FREM:
                case Constants.DREM:
                case Constants.INEG:
                case Constants.LNEG:
                case Constants.FNEG:
                case Constants.DNEG:
                case Constants.ISHL:
                case Constants.LSHL:
                case Constants.ISHR:
                case Constants.LSHR:
                case Constants.IUSHR:
                case Constants.LUSHR:
                case Constants.IAND:
                case Constants.LAND:
                case Constants.IOR:
                case Constants.LOR:
                case Constants.IXOR:
                case Constants.LXOR:
                case Constants.I2L:
                case Constants.I2F:
                case Constants.I2D:
                case Constants.L2I:
                case Constants.L2F:
                case Constants.L2D:
                case Constants.F2I:
                case Constants.F2L:
                case Constants.F2D:
                case Constants.D2I:
                case Constants.D2L:
                case Constants.D2F:
                case Constants.I2B:
                case Constants.I2C:
                case Constants.I2S:
                case Constants.LCMP:
                case Constants.FCMPL:
                case Constants.FCMPG:
                case Constants.DCMPL:
                case Constants.DCMPG:
                case Constants.IRETURN:
                case Constants.LRETURN:
                case Constants.FRETURN:
                case Constants.DRETURN:
                case Constants.ARETURN:
                case Constants.RETURN:
                case Constants.ARRAYLENGTH:
                case Constants.ATHROW:
                case Constants.MONITORENTER:
                case Constants.MONITOREXIT:
                    md.instructions.add(new Insn(location, insn));
                    break;
                case Constants.IFEQ:
                case Constants.IFNE:
                case Constants.IFLT:
                case Constants.IFGE:
                case Constants.IFGT:
                case Constants.IFLE:
                case Constants.IF_ICMPEQ:
                case Constants.IF_ICMPNE:
                case Constants.IF_ICMPLT:
                case Constants.IF_ICMPGE:
                case Constants.IF_ICMPGT:
                case Constants.IF_ICMPLE:
                case Constants.IF_ACMPEQ:
                case Constants.IF_ACMPNE:
                case Constants.GOTO:
                case Constants.JSR:
                case Constants.IFNULL:
                case Constants.IFNONNULL:
                    md.instructions.add(new JumpInsn(location, insn, parseIdentifier()));
                    break;
                case Constants.TABLESWITCH:
                case Constants.LOOKUPSWITCH:
                case Constants.SWITCH: {
                    List<SwitchCase> cases = new ArrayList<>();
                    String dflt = null;
                    read("{");
                    while (!peekRead("}")) {
                        if (peek(TokenType.INTEGER_LITERAL)) {
                            cases.add(parseSwitchCase());
                        } else if (peek("default")) {
                            Token t = read();
                            if (dflt != null) {
                                throw new CompileException("Duplicate \"default\" label", t.getLocation());
                            }
                            read(":");
                            dflt = parseIdentifier();
                        } else {
                            throw new CompileException("integer literal or \"default\" expected", location());
                        }
                    }
                    md.instructions.add(new SwitchInsn(location, cases.toArray(SwitchCase.EMPTY_ARRAY), dflt));
                    break;
                }
                case Constants.ILOAD:
                case Constants.LLOAD:
                case Constants.FLOAD:
                case Constants.DLOAD:
                case Constants.ALOAD:
                case Constants.ISTORE:
                case Constants.LSTORE:
                case Constants.FSTORE:
                case Constants.DSTORE:
                case Constants.ASTORE:
                case Constants.RET:
                    md.instructions.add(new VarInsn(location, insn, parseIntegerLiteral()));
                    break;
                case Constants.BIPUSH:
                case Constants.SIPUSH:
                case Constants.NEWARRAY: // todo: newarray type
                    md.instructions.add(new IntInsn(location, insn, parseIntegerLiteral()));
                    break;
                case Constants.LDC:
                    md.instructions.add(new LdcInsn(location, parseValue()));
                    break;
                case Constants.GETSTATIC:
                case Constants.PUTSTATIC:
                case Constants.GETFIELD:
                case Constants.PUTFIELD:
                    md.instructions.add(new FieldInsn(location, insn, parseReferenceType(), parseIdentifier(), parseType()));
                    break;
                case Constants.INVOKEVIRTUAL:
                case Constants.INVOKESPECIAL:
                case Constants.INVOKESTATIC:
                case Constants.INVOKEINTERFACE:
                    md.instructions.add(new MethodInsn(location, insn, parseReferenceType(), parseIdentifierOrInit(), parseMethodType()));
                    break;
                case Constants.INVOKEDYNAMIC:
                    read("{");
                    md.instructions.add(new InvokeDynamicInsn(location, parseIdentifier(), parseMethodType(), parseHandle(), parseBootstrapMethodArguments()));
                    read("}");
                    break;
                case Constants.NEW:
                case Constants.ANEWARRAY:
                case Constants.CHECKCAST:
                case Constants.INSTANCEOF:
                    md.instructions.add(new TypeInsn(location, insn, parseReferenceType()));
                    break;
                case Constants.IINC:
                    md.instructions.add(new IincInsn(location, parseIntegerLiteral(), parseIntegerLiteral()));
                    break;
                case Constants.MULTIANEWARRAY:
                    md.instructions.add(new MultiANewArrayInsn(location, parseType(), parseIntegerLiteral()));
                    break;
                case Constants.LINE_NUMBER:
                    md.instructions.add(new LineNumberInsn(location, parseIntegerLiteral(), parseIdentifier()));
                    break;
                case Constants.LOCAL_VARIABLE: {
                    String name = parseIdentifier();
                    Type type = parseType();
                    String start = parseIdentifier();
                    String end = parseIdentifier();
                    IntegerLiteral index = parseIntegerLiteral();
                    md.localVariables.add(new LocalVariable(location, name, type, start, end, index));
                    break;
                }
                case Constants.TRY_CATCH_BLOCK: {
                    String start = parseIdentifier();
                    String end = parseIdentifier();
                    String handle = parseIdentifier();
                    ReferenceType type = peekRead("finally") ? null : parseReferenceType();
                    md.tryCatchBlocks.add(new TryCatchBlock(location, start, end, handle, type));
                    break;
                }
                default:
                    throw new CompileException("Unknown opcode: " + insn, location);
            }
        }
    }

    private SwitchCase parseSwitchCase() {
        Location location = location();
        IntegerLiteral key = parseIntegerLiteral();
        read(":");
        String label = parseIdentifier();
        return new SwitchCase(location, key, label);
    }

    private Annotation[] parseAnnotations() {
        List<Annotation> l = new ArrayList<>();
        while (peek("@")) {
            l.add(parseAnnotation());
        }
        return l.toArray(Annotation.EMPTY_ARRAY);
    }

    private Annotation parseAnnotation() {
        Location location = location();
        read("@");
        ReferenceType type = new ReferenceType(location(), parseQualifiedIdentifier());
        AnnotationValuePair[] values = parseAnnotationValues();
        boolean visible = !peekRead("invisible");
        return new Annotation(location, type, values, visible);
    }

    private AnnotationValuePair[] parseAnnotationValues() {
        if (!peekRead("(")) {
            return AnnotationValuePair.EMPTY_ARRAY;
        }
        if (peekRead(")")) {
            return AnnotationValuePair.EMPTY_ARRAY;
        }

        Location location = location();
        if (!peek(TokenType.IDENTIFIER) || !peek2("=")) {
            AnnotationValue value = parseAnnotationValue();
            read(")");
            return new AnnotationValuePair[]{new AnnotationValuePair(location, "value", value)};
        }

        List<AnnotationValuePair> l = new ArrayList<>();
        do {
            l.add(parseAnnotationValuePair());
        } while (peekRead(","));
        read(")");
        return l.toArray(AnnotationValuePair.EMPTY_ARRAY);
    }

    private AnnotationValuePair parseAnnotationValuePair() {
        Location location = location();
        String key = parseIdentifier();
        read("=");
        return new AnnotationValuePair(location, key, parseAnnotationValue());
    }

    private AnnotationValue parseAnnotationValue() {
        if (peek("@")) return parseAnnotation();
        if (peek("{")) return parseAnnotationValueArrayInitializer();
        return parseValue();
    }

    private AnnotationValueArrayInitializer parseAnnotationValueArrayInitializer() {
        Location location = location();
        read("{");
        if (peekRead("}")) {
            return new AnnotationValueArrayInitializer(location, AnnotationValue.EMPTY_ARRAY);
        }
        List<AnnotationValue> values = new ArrayList<>();
        while (!peekRead("}")) {
            values.add(parseAnnotationValue());
            if (peekRead("}")) break;
            read(",");
        }
        return new AnnotationValueArrayInitializer(location, values.toArray(AnnotationValue.EMPTY_ARRAY));
    }

    private Modifier[] parseModifiers() {
        List<Modifier> result = new ArrayList<>();
        for (; ; ) {
            Modifier m = parseModifier();
            if (m == null) break;
            result.add(m);
        }
        return result.toArray(Modifier.EMPTY_ARRAY);
    }

    private Modifier parseModifier() {
        Location location = location();
        if (peekRead("@")) {
            read("interface");
            return new Modifier(location, "@interface");
        }
        int index = peekRead(MODIFIERS);
        if (index == -1) return null;
        return new Modifier(location, MODIFIERS[index]);
    }

    private String parseIdentifier() {
        return read(TokenType.IDENTIFIER).getText();
    }

    private String parseIdentifierOrInit() {
        Location location = location();
        if (peekRead("<")) {
            if (peek("init") && peek2(">")) {
                read();
                read();
                return "<init>";
            }
            throw new CompileException("Unexpected token \"<\"", location);
        }
        return parseIdentifier();
    }

    private String[] parseQualifiedIdentifier() {
        List<String> result = new ArrayList<>();
        do {
            result.add(parseIdentifier());
        } while (peekRead("."));
        return result.toArray(Constants.EMPTY_STRING_ARRAY);
    }

    private Type parseVoidOrType() {
        return peek("void") ? parseVoid() : parseType();
    }

    private Type parseVoid() {
        return new PrimitiveType(read("void").getLocation(), Primitive.VOID);
    }

    private Type parseType() {
        Location location = location();
        Type type;
        switch (peekRead(PRIMITIVES)) {
            case 0:
                type = new PrimitiveType(location, Primitive.BOOLEAN);
                break;
            case 1:
                type = new PrimitiveType(location, Primitive.CHAR);
                break;
            case 2:
                type = new PrimitiveType(location, Primitive.BYTE);
                break;
            case 3:
                type = new PrimitiveType(location, Primitive.SHORT);
                break;
            case 4:
                type = new PrimitiveType(location, Primitive.INT);
                break;
            case 5:
                type = new PrimitiveType(location, Primitive.FLOAT);
                break;
            case 6:
                type = new PrimitiveType(location, Primitive.LONG);
                break;
            case 7:
                type = new PrimitiveType(location, Primitive.DOUBLE);
                break;
            case -1:
                type = parseReferenceType();
                break;
            default:
                throw new InternalCompileException();
        }

        for (int i = parseBrackets(); i > 0; i--) {
            type = new ArrayType(location, type);
        }
        return type;
    }

    private ReferenceType parseReferenceType() {
        return new ReferenceType(location(), parseQualifiedIdentifier());
    }

    private int parseBrackets() {
        int count = 0;
        while (peek("[") && peek2("]")) {
            read();
            read();
            count++;
        }
        return count;
    }

    private ReferenceType parseSuperclass() {
        if (peekRead("extends")) {
            return parseReferenceType();
        }
        return null;
    }

    private ReferenceType[] parseInterfaces() {
        if (peekRead("implements")) {
            List<ReferenceType> result = new ArrayList<>();
            do {
                result.add(parseReferenceType());
            } while (peekRead(","));
            return result.toArray(ReferenceType.EMPTY_ARRAY);
        }
        return ReferenceType.EMPTY_ARRAY;
    }

    private Value parseValue() {
        // Parse handle
        if (peek("Handle")) {
            return parseHandle();
        }

        // Parse constant dynamic
        if (peek("ConstantDynamic")) {
            return parseConstantDynamic();
        }

        // Parse method type
        if (peek("(")) {
            return parseMethodType();
        }

        // Parse literal
        if (peek(LITERALS) != -1) {
            return parseLiteral();
        }

        Location location = location();

        // Parse void class
        if (peekRead("void")) {
            if (peek(".") && peek2("class")) {
                read();
                read();
                return new ClassLiteral(location, new PrimitiveType(location, Primitive.VOID));
            }
            throw new CompileException("Unexpected token \"void\"", location);
        }

        // Parse primitive class
        if (peek(PRIMITIVES) != -1) {
            Type type = parseType();
            if (peek(".") && peek2("class")) {
                read();
                read();
                return new ClassLiteral(location, type);
            }
            throw new CompileException("Unexpected token", location);
        }

        // Parse reference class or ambiguous name
        if (peek(TokenType.IDENTIFIER)) {
            List<String> identifiers = new ArrayList<>();
            identifiers.add(parseIdentifier());
            while (peekRead(".")) {
                String identifier = parseIdentifier();
                if ("class".equals(identifier)) {
                    return new ClassLiteral(location, new ReferenceType(location, identifiers.toArray(Constants.EMPTY_STRING_ARRAY)));
                } else {
                    identifiers.add(identifier);
                }
            }
            return new AmbiguousName(location, identifiers.toArray(Constants.EMPTY_STRING_ARRAY));
        }

        throw new CompileException("Unexpected token \"" + read().getText() + "\"", location);
    }

    private MethodType parseMethodType() {
        return new MethodType(location(), parseMethodParameterTypes(), parseVoidOrType());
    }

    private Handle parseHandle() {
        Location location = location();
        read("Handle");
        read("{");
        String kind = parseIdentifier();
        ReferenceType owner = parseReferenceType();
        String name = parseIdentifier();
        HandleType type;
        switch (kind) {
            case "getfield":
            case "getstatic":
            case "putfield":
            case "putstatic":
                type = parseType();
                break;
            case "invokevirtual":
            case "invokestatic":
            case "invokespecial":
            case "newinvokespecial":
            case "invokeinterface":
            case "invokevirtualinterface":
            case "invokestaticinterface":
            case "invokespecialinterface":
            case "newinvokespecialinterface":
                type = parseMethodType();
                break;
            default:
                throw new CompileException("Unknown handle kind \"" + kind + "\"", location);
        }
        read("}");
        return new Handle(location, kind, owner, name, type);
    }

    private ConstantDynamic parseConstantDynamic() {
        Location location = location();
        read("ConstantDynamic");
        read("{");
        String identifier = parseIdentifier();
        Type type = parseType();
        Handle bootstrapMethod = parseHandle();
        Value[] bootstrapMethodArguments = parseBootstrapMethodArguments();
        read("}");
        return new ConstantDynamic(location, identifier, type, bootstrapMethod, bootstrapMethodArguments);
    }

    private Value[] parseBootstrapMethodArguments() {
        List<Value> arguments = new ArrayList<>();
        read("{");
        do {
            arguments.add(parseValue());
        } while (peekRead(","));
        read("}");
        return arguments.toArray(Value.EMPTY_ARRAY);
    }

    private Literal parseLiteral() {
        Token t = read();
        switch (t.getType()) {
            case NULL_LITERAL:
                return new NullLiteral(t.getLocation());
            case BOOLEAN_LITERAL:
                return new BooleanLiteral(t.getLocation(), t.getText());
            case CHARACTER_LITERAL:
                return new CharacterLiteral(t.getLocation(), t.getText());
            case INTEGER_LITERAL:
                return new IntegerLiteral(t.getLocation(), t.getText());
            case FLOATING_POINT_LITERAL:
                return new FloatingPointLiteral(t.getLocation(), t.getText());
            case STRING_LITERAL:
                return new StringLiteral(t.getLocation(), t.getText());
            default:
                throw new CompileException("Expected literal", t.getLocation());
        }
    }

    private IntegerLiteral parseIntegerLiteral() {
        Token t = read(TokenType.INTEGER_LITERAL);
        return new IntegerLiteral(t.getLocation(), t.getText());
    }

    private StringLiteral parseStringLiteral() {
        Token t = read(TokenType.STRING_LITERAL);
        return new StringLiteral(t.getLocation(), t.getText());
    }

    private static boolean hasModifier(Modifier[] modifiers, String keyword) {
        for (Modifier modifier : modifiers) {
            if (modifier.keyword.equals(keyword)) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasModifier(Modifier[] modifiers, String... keywords) {
        for (Modifier modifier : modifiers) {
            for (String keyword : keywords) {
                if (modifier.keyword.equals(keyword)) {
                    return true;
                }
            }
        }
        return false;
    }

    private Location location() {
        return tokenStream.location();
    }

    private Token peek() {
        return tokenStream.peek();
    }

    private boolean peek(String expected) {
        return tokenStream.peek(expected);
    }

    private boolean peek(TokenType expected) {
        return tokenStream.peek(expected);
    }

    private int peek(String... suspected) {
        return tokenStream.peek(suspected);
    }

    private int peek(TokenType... suspected) {
        return tokenStream.peek(suspected);
    }

    private Token peek2() {
        return tokenStream.peek2();
    }

    private boolean peek2(String expected) {
        return tokenStream.peek2(expected);
    }

    private boolean peek2(TokenType expected) {
        return tokenStream.peek2(expected);
    }

    private int peek2(String... suspected) {
        return tokenStream.peek2(suspected);
    }

    private int peek2(TokenType... suspected) {
        return tokenStream.peek2(suspected);
    }

    private Token read() {
        return tokenStream.read();
    }

    private Token read(String expected) throws CompileException {
        return tokenStream.read(expected);
    }

    private Token read(TokenType expected) throws CompileException {
        return tokenStream.read(expected);
    }

    private int read(String... suspected) throws CompileException {
        return tokenStream.read(suspected);
    }

    private Token read(TokenType... suspected) throws CompileException {
        return tokenStream.read(suspected);
    }

    private boolean peekRead(String expected) {
        return tokenStream.peekRead(expected);
    }

    private boolean peekRead(TokenType expected) {
        return tokenStream.peekRead(expected);
    }

    private int peekRead(String... suspected) {
        return tokenStream.peekRead(suspected);
    }

    private int peekRead(TokenType... suspected) {
        return tokenStream.peekRead(suspected);
    }
}
