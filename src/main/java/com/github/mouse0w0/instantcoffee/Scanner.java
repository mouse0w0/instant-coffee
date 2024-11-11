package com.github.mouse0w0.instantcoffee;

import java.io.IOException;
import java.io.Reader;

public class Scanner {
    private static final int EOF = -1;

    private final Reader in;
    private final String fileName;

    private int nextCharLineNumber = 1;
    private int nextCharColumnNumber = 0;
    private boolean crlfPending;

    private int nextChar = -1;
    private int next2Char = -1;

    private final StringBuilder sb = new StringBuilder();

    public Scanner(Reader in) {
        this(in, "<unknown>");
    }

    public Scanner(Reader in, String fileName) {
        this.in = in;
        this.fileName = fileName;
    }

    public Token produce() {
        if (peek() == EOF) {
            return new Token(location(), TokenType.END_OF_FILE, "end-of-file");
        }

        sb.setLength(0);

        Location location = location();
        TokenType tokenType = scan();
        String tokenValue = sb.toString();

        if (tokenType == TokenType.BOOLEAN_LITERAL || tokenType == TokenType.NULL_LITERAL || tokenType == TokenType.OPERATOR) {
            tokenValue = tokenValue.intern();
        }

        return new Token(location, tokenType, tokenValue);
    }

    private TokenType scan() {
        // Scan white space
        if (Character.isWhitespace(peek())) {
            do {
                read();
            } while (Character.isWhitespace(peek()));
            return TokenType.WHITE_SPACE;
        }

        if (peekRead('/')) {
            // Scan single-line comment
            if (peekRead('/')) {
                while (peek() != '\n' && peek() != EOF) read();
                return TokenType.SINGLE_LINE_COMMENT;
            }

            // Scan multi-line comment
            if (peekRead('*')) {
                for (; ; ) {
                    if (peek() == EOF) {
                        throw new CompileException("Unexpected EOF in multiline comment", location());
                    }

                    if (read() == '*' && peekRead('/')) {
                        return TokenType.MULTI_LINE_COMMENT;
                    }
                }
            }

            throw new CompileException("Unexpected character '/'", location());
        }

        // Scan identifier
        if (Character.isJavaIdentifierStart(peek())) {
            read();
            while (Character.isJavaIdentifierPart(peek())) read();

            String s = sb.toString();
            if ("true".equals(s)) return TokenType.BOOLEAN_LITERAL;
            if ("false".equals(s)) return TokenType.BOOLEAN_LITERAL;
            if ("null".equals(s)) return TokenType.NULL_LITERAL;
            return TokenType.IDENTIFIER;
        }

        // Scan numeric literal
        if (peek() == '-' || isDecimalDigit(peek()) || (peek() == '.' && isDecimalDigit(peek2()))) {
            return scanNumericLiteral();
        }

        // Scan string literal
        if (peekRead('"')) {
            while (!peekRead('"')) scanLiteralCharacter();
            return TokenType.STRING_LITERAL;
        }

        // Scan character literal
        if (peekRead('\'')) {
            if (peek() == '\'') {
                throw new CompileException("Empty character literal", location());
            }

            scanLiteralCharacter();
            if (!peekRead('\'')) {
                throw new CompileException("Unclosed character literal", location());
            }

            return TokenType.CHARACTER_LITERAL;
        }

        // Scan operator
        if (peekRead("{}[]()<>,.@=:#")) {
            return TokenType.OPERATOR;
        }

        throw new CompileException("Unexpected character '" + (char) peek() + "' (character code " + peek() + " )", location());
    }

    private TokenType scanNumericLiteral() {
        peekRead('-');

        if (peekRead('0')) {
            // 0L
            if (peekRead("bBsSlL")) return TokenType.INTEGER_LITERAL;

            // 0F or 0D
            if (peekRead("fFdD")) return TokenType.FLOATING_POINT_LITERAL;

            // Scan hexadecimal literal
            if (peekRead("xX")) {
                if (!isHexDigit(peek())) {
                    throw new CompileException("Hexadecimal digit expected after \"0x\"", location());
                }
                read();

                while (isHexDigit(peek()) || (peek() == '_' && (peek2() == '_' || isHexDigit(peek2())))) {
                    read();
                }

                if (peek(".pP")) {
                    if (peekRead('.')) {
                        if (isHexDigit(peek())) {
                            read();
                            while (isHexDigit(peek()) || (peek() == '_' && (peek2() == '_' || isHexDigit(peek2())))) {
                                read();
                            }
                        }
                    }

                    if (!peekRead("pP")) {
                        throw new CompileException("'p' missing in hexadecimal floating-point literal", location());
                    }

                    peekRead("-+");

                    if (!isDecimalDigit(peek())) {
                        throw new CompileException("Decimal digit expected after 'p'", location());
                    }
                    read();

                    while (isDecimalDigit(peek()) || (peek() == '_' && (peek2() == '_' || isDecimalDigit(peek2())))) {
                        read();
                    }

                    peekRead("fFdD");
                    return TokenType.FLOATING_POINT_LITERAL;
                }

                peekRead("bBsSlL");
                return TokenType.INTEGER_LITERAL;
            }

            // Scan binary literal
            if (peekRead("bB")) {
                if (!isBinaryDigit(peek())) {
                    throw new CompileException("Binary digit expected after \"0b\"", location());
                }
                read();

                while (isBinaryDigit(peek()) || (peek() == '_' && (peek2() == '_' || isBinaryDigit(peek2())))) {
                    read();
                }

                if (isDecimalDigit(peek())) {
                    throw new CompileException("Digit '" + (char) peek() + "' not allowed in binary literal", location());
                }

                peekRead("bBsSlL");
                return TokenType.INTEGER_LITERAL;
            }

            // Scan octal literal
            if (isOctalDigit(peek()) || (peek() == '_' && (peek2() == '_' || isOctalDigit(peek2())))) {
                read();
                while (isOctalDigit(peek()) || (peek() == '_' && (peek2() == '_' || isOctalDigit(peek2())))) {
                    read();
                }

                if (isDecimalDigit(peek())) {
                    throw new CompileException("Digit '" + (char) peek() + "' not allowed in octal literal", location());
                }

                peekRead("bBsSlL");
                return TokenType.INTEGER_LITERAL;
            }

            if (peek(".eE")) {
                scanFraction();
                scanExponent();
                peekRead("fFdD");
                return TokenType.FLOATING_POINT_LITERAL;
            }

            return TokenType.INTEGER_LITERAL;
        }

        if (isDecimalDigit(peek())) {
            read();

            while (isDecimalDigit(peek()) || (peek() == '_' && (peek2() == '_' || isDecimalDigit(peek2())))) read();

            if (peekRead("bBsSlL")) return TokenType.INTEGER_LITERAL;

            if (peekRead("fFdD")) return TokenType.FLOATING_POINT_LITERAL;

            if (!peek(".eE")) return TokenType.INTEGER_LITERAL;
        }

        scanFraction();
        scanExponent();
        peekRead("fFdD");
        return TokenType.FLOATING_POINT_LITERAL;
    }

    private void scanFraction() {
        if (peekRead('.')) {
            if (!isDecimalDigit(peek())) {
                throw new CompileException("Decimal digit expected after '.'", location());
            }

            read();
            while (isDecimalDigit(peek()) || (peek() == '_' && (peek2() == '_' || isDecimalDigit(peek2())))) {
                read();
            }
        }
    }

    private void scanExponent() {
        if (peekRead("eE")) {

            peekRead("-+");

            if (!isDecimalDigit(peek())) {
                throw new CompileException("Decimal digit expected after 'E'", location());
            }
            read();
            while (isDecimalDigit(peek()) || (peek() == '_' && (peek2() == '_' || isDecimalDigit(peek2())))) {
                read();
            }
        }
    }

    private static boolean isDecimalDigit(int c) {
        return c >= '0' && c <= '9';
    }

    private static boolean isHexDigit(int c) {
        return (c >= '0' && c <= '9') || (c >= 'A' && c <= 'F') || (c >= 'a' && c <= 'f');
    }

    private static boolean isOctalDigit(int c) {
        return c >= '0' && c <= '7';
    }

    private static boolean isBinaryDigit(int c) {
        return c == '0' || c == '1';
    }

    private void scanLiteralCharacter() {
        if (peek() == EOF) throw new CompileException("EOF in literal", location());
        if (peek() == '\n') throw new CompileException("Line break in literal", location());

        if (read() == '\\') {
            if (peekRead("bfnrt\\'\"")) return;

            if (isOctalDigit(peek())) {
                int firstChar = read();

                if (!isOctalDigit(peek())) return;
                read();

                if (!isOctalDigit(peek())) return;
                read();

                if (firstChar >= '0' && firstChar <= '3') return;

                throw new CompileException("Invalid octal escape", location());
            }

            throw new CompileException("Illegal escape character '" + peek() + "'", location());
        }
    }

    public Location location() {
        return new Location(fileName, nextCharLineNumber, nextCharColumnNumber);
    }

    private boolean peek(String expectedCharacters) {
        return expectedCharacters.indexOf(peek()) != -1;
    }

    private boolean peekRead(int expected) {
        final int result = peek();
        if (result != expected) return false;
        nextChar = next2Char;
        next2Char = -1;
        sb.append((char) result);
        return true;
    }

    private boolean peekRead(String expectedCharacters) {
        final int result = peek();
        if (result == -1) return false;
        if (expectedCharacters.indexOf(result) == -1) return false;
        nextChar = next2Char;
        next2Char = -1;
        sb.append((char) result);
        return true;
    }

    private int read() {
        final int result = peek();
        if (result == -1) {
            throw new CompileException("Unexpected end-of-file", location());
        }
        nextChar = next2Char;
        next2Char = -1;
        sb.append((char) result);
        return result;
    }

    private int peek() {
        if (nextChar != -1) return nextChar;
        return nextChar = read0();
    }

    private int peek2() {
        if (next2Char != -1) return next2Char;
        if (nextChar == -1) nextChar = read0();
        return next2Char = read0();
    }

    private int read0() {
        try {
            int c = in.read();
            if (crlfPending) {
                if (c == '\n') c = in.read();
                crlfPending = false;
            }
            nextCharColumnNumber++;
            if (c == '\r') {
                crlfPending = true;
                nextCharLineNumber++;
                nextCharColumnNumber = 0;
                return '\n';
            } else if (c == '\n') {
                nextCharLineNumber++;
                nextCharColumnNumber = 0;
            }
            return c;
        } catch (IOException e) {
            throw new CompileException(e.getMessage(), location(), e);
        }
    }
}
