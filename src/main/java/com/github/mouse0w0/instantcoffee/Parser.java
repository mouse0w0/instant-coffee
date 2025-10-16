package com.github.mouse0w0.instantcoffee;

import com.github.mouse0w0.instantcoffee.model.*;
import com.github.mouse0w0.instantcoffee.model.statement.*;

import java.io.Reader;
import java.io.StringReader;
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
            TokenType.BOOLEAN_LITERAL,
            TokenType.CHARACTER_LITERAL,
            TokenType.INTEGER_LITERAL,
            TokenType.FLOATING_POINT_LITERAL,
            TokenType.STRING_LITERAL
    };

    private final TokenStream tokenStream;

    public Parser(String source) {
        this(new TokenStream(new Scanner(new StringReader(source))));
    }

    public Parser(Reader reader) {
        this(new TokenStream(new Scanner(reader)));
    }

    public Parser(Scanner scanner) {
        this(new TokenStream(scanner));
    }

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
        if (peek("innerclass") || peek("local") || peek("anonymous")) {
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
            Type returnType = new VoidType(location);
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
        InnerClassType type;
        if (peekRead("anonymous")) {
            read("innerclass");
            type = InnerClassType.ANONYMOUS;
            return new InnerClassDeclaration(location, type, modifiers, parseQualifiedIdentifier(), null);
        }

        if (peekRead("local")) {
            type = InnerClassType.LOCAL;
        } else {
            type = InnerClassType.MEMBER_OR_STATIC;
        }
        read("innerclass");
        return new InnerClassDeclaration(location, type, modifiers, parseQualifiedIdentifier(), parseIdentifier());
    }

    private FieldDeclaration parseFieldDeclaration(Location location, Annotation[] annotations, Modifier[] modifiers, Type type, String name) {
        return new FieldDeclaration(location, annotations, modifiers, type, name, peekRead("=") ? parseValue() : null);
    }

    private MethodDeclaration parseInitializerDeclaration(Location location, Annotation[] annotations, Modifier[] modifiers) {
        return new MethodDeclaration(
                location,
                annotations,
                modifiers,
                new VoidType(location),
                "<clinit>",
                Type.EMPTY_ARRAY,
                ReferenceType.EMPTY_ARRAY,
                null,
                parseMethodBody(hasModifier(modifiers, "abstract", "native"))
        );
    }

    private MethodDeclaration parseMethodDeclaration(Location location, Annotation[] annotations, Modifier[] modifiers, Type returnType, String name, boolean allowDefaultClause) {
        return new MethodDeclaration(
                location,
                annotations,
                modifiers,
                returnType,
                name,
                parseMethodParameterTypes(),
                parseMethodExceptionTypes(),
                allowDefaultClause && peekRead("default") ? parseAnnotationValue() : null,
                parseMethodBody(hasModifier(modifiers, "abstract", "native"))
        );
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

    private Block parseMethodBody(boolean isAbstractOrNative) {
        if (!peek("{")) {
            if (isAbstractOrNative) {
                return null;
            }
            throw new CompileException("Method must have a body", location());
        }
        if (isAbstractOrNative) {
            throw new CompileException("Abstract or native method must not have a body", location());
        }
        return parseBlock();
    }

    private Statement parseStatement() {
        if (peek("{")) return parseBlock();
        if (peek("var")) return parseLocalVariable();
        if (peek("try")) return parseTryCatchBlock();
        if (peek("line")) return parseLineNumber();
        if (peek(TokenType.IDENTIFIER) && peek2(":")) return parseLabel();
        return parseBaseInsn();
    }

    private Block parseBlock() {
        Location location = read("{").getLocation();
        List<Statement> statements = new ArrayList<>();
        while (!peekRead("}")) {
            statements.add(parseStatement());
        }
        return new Block(location, statements);
    }

    private LocalVariable parseLocalVariable() {
        return new LocalVariable(
                read("var").getLocation(),
                parseIdentifier(),
                parseType(),
                parseIdentifier(),
                parseIdentifier(),
                parseIntegerLiteral()
        );
    }


    private TryCatchBlock parseTryCatchBlock() {
        return new TryCatchBlock(
                read("try").getLocation(),
                parseIdentifier(),
                parseIdentifier(),
                parseIdentifier(),
                peekRead("finally") ? null : parseReferenceType()
        );
    }

    private LineNumber parseLineNumber() {
        return new LineNumber(
                read("line").getLocation(),
                parseIntegerLiteral(),
                parseIdentifier()
        );
    }

    private Label parseLabel() {
        Location location = location();
        String name = parseIdentifier();
        read(":");
        return new Label(location, name);
    }

    private BaseInsn parseBaseInsn() {
        Location location = location();
        String insn = parseIdentifier();
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
                return new Insn(location, insn);
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
                return new JumpInsn(location, insn, parseIdentifier());
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
                return new SwitchInsn(location, cases.toArray(SwitchCase.EMPTY_ARRAY), dflt);
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
                return new VarInsn(location, insn, parseIntegerLiteral());
            case Constants.BIPUSH:
            case Constants.SIPUSH:
                return new IntInsn(location, insn, parseIntegerLiteral());
            case Constants.LDC:
                return new LdcInsn(location, parseValue());
            case Constants.GETSTATIC:
            case Constants.PUTSTATIC:
            case Constants.GETFIELD:
            case Constants.PUTFIELD:
                return new FieldInsn(location, insn, parseReferenceType(), parseIdentifier(), parseType());
            case Constants.INVOKEVIRTUAL:
            case Constants.INVOKESPECIAL:
            case Constants.INVOKESTATIC:
            case Constants.INVOKEINTERFACE:
                return new MethodInsn(location, insn, parseReferenceType(), parseIdentifierOrInit(), parseMethodType());
            case Constants.INVOKEDYNAMIC:
                read("{");
                String name = parseIdentifier();
                MethodType methodType = parseMethodType();
                Handle bootstrapMethod = parseHandle();
                Value[] bootstrapMethodArguments = parseBootstrapMethodArguments();
                read("}");
                return new InvokeDynamicInsn(location, name, methodType, bootstrapMethod, bootstrapMethodArguments);
            case Constants.NEW:
            case Constants.ANEWARRAY:
            case Constants.CHECKCAST:
            case Constants.INSTANCEOF:
                return new TypeInsn(location, insn, parseReferenceType());
            case Constants.IINC:
                return new IincInsn(location, parseIntegerLiteral(), parseIntegerLiteral());
            case Constants.NEWARRAY:
                return new NewArrayInsn(location, parsePrimitiveType());
            case Constants.MULTIANEWARRAY:
                return new MultiANewArrayInsn(location, parseType(), parseIntegerLiteral());
            default:
                throw new CompileException("Unknown opcode: " + insn, location);
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
        while (peek("@") && !peek2("interface")) {
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
        if (peek(LITERALS) != -1) return parseLiteral();
        Type type = parseVoidOrType();
        if (peek("#") && type instanceof ReferenceType) {
            read();
            return new EnumLiteral(type.getLocation(), (ReferenceType) type, parseIdentifier());
        }
        return type;
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
        return new VoidType(read("void").getLocation());
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

    private PrimitiveType parsePrimitiveType() {
        Location location = location();
        switch (read(PRIMITIVES)) {
            case 0:
                return new PrimitiveType(location, Primitive.BOOLEAN);
            case 1:
                return new PrimitiveType(location, Primitive.CHAR);
            case 2:
                return new PrimitiveType(location, Primitive.BYTE);
            case 3:
                return new PrimitiveType(location, Primitive.SHORT);
            case 4:
                return new PrimitiveType(location, Primitive.INT);
            case 5:
                return new PrimitiveType(location, Primitive.FLOAT);
            case 6:
                return new PrimitiveType(location, Primitive.LONG);
            case 7:
                return new PrimitiveType(location, Primitive.DOUBLE);
            default:
                throw new InternalCompileException();
        }
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
        if (peek("Handle")) return parseHandle();
        if (peek("ConstantDynamic")) return parseConstantDynamic();
        if (peek("(")) return parseMethodType();
        if (peek(TokenType.NULL_LITERAL)) return new NullLiteral(read().getLocation());
        if (peek(LITERALS) != -1) return parseLiteral();
        return parseVoidOrType();
    }

    private Literal parseLiteral() {
        Token t = read();
        switch (t.getType()) {
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
