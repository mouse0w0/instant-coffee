package com.github.mouse0w0.instantcoffee;

import com.github.mouse0w0.instantcoffee.model.*;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    private static final String[] EMPTY_STRING_ARRAY = new String[0];
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
//            "@interface", // 0x2000
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
                parseTypeParameters(),
                parseSuperclass(),
                parseInterfaces());

        parseClassBody(classDeclaration);

        return classDeclaration;
    }

    private void parseClassBody(ClassDeclaration cd) {
        read("{");
        while (!peekRead("}")) {
            if (peek("source")) {
                cd.source = parseSourceDeclaration();
            }

            Annotation[] annotations = parseAnnotations();
            Modifier[] modifiers = parseModifiers();
            if (peek("innerclass")) {
                if (annotations.length != 0) {
                    throw new CompileException("Inner class cannot be annotated", location());
                }
                cd.innerClasses.add(parseInnerClassDeclaration(modifiers));
            }

            TypeParameter[] typeParameters = parseTypeParameters();
            Type type = parseType();
            Location location = location();
            String name = read(TokenType.IDENTIFIER).getText();
            if (peek("(")) {
                cd.methods.add(parseMethodDeclaration(location, annotations, modifiers, typeParameters, type, name));
            }

            if (typeParameters != null) {
                throw new CompileException("Type parameters not allowed on field declaration", location);
            }
            cd.fields.add(parseFieldDeclaration(location, annotations, modifiers, type, name));
        }
        read("}");
    }

    private SourceDeclaration parseSourceDeclaration() {
        Location location = location();
        read("source");
        return new SourceDeclaration(location, parseStringLiteral());
    }

    private InnerClassDeclaration parseInnerClassDeclaration(Modifier[] modifiers) {
        Location location = location();
        read("innerclass");
        return new InnerClassDeclaration(location, modifiers, parseQualifiedIdentifier());
    }

    private FieldDeclaration parseFieldDeclaration(Location location, Annotation[] annotations, Modifier[] modifiers, Type type, String name) {
        return new FieldDeclaration(location, annotations, modifiers, type, name, peekRead("=") ? parseValue() : null);
    }

    private MethodDeclaration parseMethodDeclaration(Location location, Annotation[] annotations, Modifier[] modifiers, TypeParameter[] typeParameters, Type returnType, String name) {
        read("(");
        List<Type> parameterTypes = new ArrayList<>();
        do {
            parameterTypes.add(parseType());
        } while (peekRead(","));
        read(")");

        List<ReferenceType> exceptionTypes = new ArrayList<>();
        if (peekRead("throws")) {
            do {
                exceptionTypes.add(parseReferenceType());
            } while (peekRead(","));
        }

        MethodDeclaration methodDeclaration = new MethodDeclaration(
                location,
                annotations,
                modifiers,
                typeParameters,
                returnType,
                name,
                parameterTypes.toArray(Type.EMPTY_ARRAY),
                exceptionTypes.toArray(ReferenceType.EMPTY_ARRAY));
        parseMethodBody(methodDeclaration);
        return methodDeclaration;
    }

    private void parseMethodBody(MethodDeclaration md) {
        read("{");
        while (!peekRead("}")) {

        }
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
        String key = read(TokenType.IDENTIFIER).getText();
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

    private String[] parseQualifiedIdentifier() {
        List<String> result = new ArrayList<>();
        result.add(read(TokenType.IDENTIFIER).getText());
        while (peek(".") && peek2(TokenType.IDENTIFIER)) {
            read();
            result.add(read(TokenType.IDENTIFIER).getText());
        }
        return result.toArray(EMPTY_STRING_ARRAY);
    }

    private TypeParameter[] parseTypeParameters() {
        if (!peekRead("<")) {
            return null;
        }
        if (peekRead(">")) {
            return TypeParameter.EMPTY_ARRAY;
        }
        List<TypeParameter> result = new ArrayList<>();
        do {
            result.add(parseTypeParameter());
        } while (peekRead(","));
        read(">");
        return result.toArray(TypeParameter.EMPTY_ARRAY);
    }

    private TypeParameter parseTypeParameter() {
        Location location = location();
        String name = read(TokenType.IDENTIFIER).getText();
        if (peekRead("extends")) {
            List<ReferenceType> bounds = new ArrayList<>();
            bounds.add(parseReferenceType());
            while (peekRead("&")) {
                bounds.add(parseReferenceType());
            }
            return new TypeParameter(location, name, bounds.toArray(ReferenceType.EMPTY_ARRAY));
        } else if (peekRead("implements")) {
            List<ReferenceType> bounds = new ArrayList<>();
            bounds.add(null);
            bounds.add(parseReferenceType());
            while (peekRead("&")) {
                bounds.add(parseReferenceType());
            }
            return new TypeParameter(location, name, bounds.toArray(ReferenceType.EMPTY_ARRAY));
        }
        return new TypeParameter(location, name);
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
        return new ReferenceType(location(), parseQualifiedIdentifier(), parseTypeArguments());
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

    private TypeArgument[] parseTypeArguments() {
        if (!peekRead("<")) {
            return TypeArgument.EMPTY_ARRAY;
        }
        if (peekRead(">")) {
            return TypeArgument.EMPTY_ARRAY;
        }
        List<TypeArgument> result = new ArrayList<>();
        do {
            result.add(parseTypeArgument());
        } while (peekRead(","));
        read(">");
        return result.toArray(TypeArgument.EMPTY_ARRAY);
    }

    private TypeArgument parseTypeArgument() {
        Location location = location();
        if (peekRead("?")) {
            if (peekRead("extends")) {
                return new Wildcard(location, Wildcard.BOUNDS_EXTENDS, parseReferenceType());
            } else if (peekRead("super")) {
                return new Wildcard(location, Wildcard.BOUNDS_SUPER, parseReferenceType());
            } else {
                return new Wildcard(location);
            }
        }

        Type type = parseType();
        if (!(type instanceof TypeArgument)) {
            throw new CompileException("'" + type + "' is not a type argument", location());
        }

        return (TypeArgument) type;
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
        Location location = location();
        if (peekRead("(")) {
            if (peek(PRIMITIVES) != -1 && !peek2(TokenType.IDENTIFIER)) {
                Type type = parseType();
                Value value = parseValue();
                read(")");
                return new Cast(location, type, value);
            }
        }

        if (peek(LITERALS) != -1) {
            return parseLiteral();
        }

        if (peek(TokenType.IDENTIFIER)) {
            String[] identifiers = parseQualifiedIdentifier();
            if (peek(".") && peek2("class")) {
                return new ClassLiteral(location, new ReferenceType(location, identifiers));
            }
            return new AmbiguousName(location, identifiers);
        }

        if (peek(PRIMITIVES) != -1) {
            Type type = parseType();
            if (peek(".") && peek2("class")) {
                return new ClassLiteral(location, type);
            }
            throw new CompileException("Unexpected token", location);
        }

        if (peek("void")) {
            Type type = new PrimitiveType(location, Primitive.VOID);
            if (peek(".") && peek2("class")) {
                return new ClassLiteral(location, type);
            }
            throw new CompileException("Unexpected token \"void\"", location);
        }

        throw new CompileException("Unexpected token \"" + read().getText() + "\"", location);
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

    private StringLiteral parseStringLiteral() {
        Token t = read(TokenType.STRING_LITERAL);
        return new StringLiteral(t.getLocation(), t.getText());
    }

    private boolean hasModifier(Modifier[] modifiers, String keyword) {
        for (Modifier modifier : modifiers) {
            if (modifier.keyword.equals(keyword)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasModifierOneOf(Modifier[] modifiers, String... keywords) {
        for (String keyword : keywords) {
            for (Modifier modifier : modifiers) {
                if (modifier.keyword.equals(keyword)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasModifierAllOf(Modifier[] modifiers, String... keywords) {
        NEXT:
        for (String keyword : keywords) {
            for (Modifier modifier : modifiers) {
                if (modifier.keyword.equals(keyword)) {
                    continue NEXT;
                }
            }
            return false;
        }
        return true;
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

    private void read(String expected) throws CompileException {
        tokenStream.read(expected);
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
