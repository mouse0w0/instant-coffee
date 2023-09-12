package com.github.mouse0w0.instantcoffee;

public class TokenStream {
    private final Scanner scanner;

    private Token nextToken;
    private Token next2Token;

    public TokenStream(Scanner scanner) {
        this.scanner = scanner;
    }

    public Location location() {
        return nextToken != null ? nextToken.getLocation() : scanner.location();
    }

    public Token peek() {
        if (nextToken != null) return nextToken;
        return nextToken = produce();
    }

    public boolean peek(String expected) {
        return expected.equals(peek().getText());
    }

    public boolean peek(TokenType expected) {
        return expected.equals(peek().getType());
    }

    public int peek(String... suspected) {
        return indexOf(suspected, peek().getText());
    }

    public int peek(TokenType... suspected) {
        return indexOf(suspected, peek().getType());
    }

    public Token peek2() {
        if (next2Token != null) return next2Token;
        peek();
        return next2Token = produce();
    }

    public boolean peek2(String expected) {
        return expected.equals(peek2().getText());
    }

    public boolean peek2(TokenType expected) {
        return expected.equals(peek2().getType());
    }

    public int peek2(String... suspected) {
        return indexOf(suspected, peek2().getText());
    }

    public int peek2(TokenType... suspected) {
        return indexOf(suspected, peek2().getType());
    }

    public Token read() {
        final Token result = peek();

        nextToken = next2Token;
        next2Token = null;

        return result;
    }

    public Token read(String expected) throws CompileException {
        final Token token = read();
        if (!expected.equals(token.getText())) {
            throw new CompileException("\"" + expected + "\" expected instead of \"" + token.getText() + "\"", token.getLocation());
        }
        return token;
    }

    public Token read(TokenType expected) throws CompileException {
        final Token token = read();
        if (!expected.equals(token.getType())) {
            throw new CompileException(expected + " expected instead of " + token.getType(), token.getLocation());
        }
        return token;
    }

    public int read(String... suspected) throws CompileException {
        final Token token = read();
        final int result = indexOf(suspected, token.getText());
        if (result == -1) {
            throw new CompileException("One of " + join(suspected, ", ") + " expected instead of \"" + token.getText() + "\"", token.getLocation());
        }
        return result;
    }

    public Token read(TokenType... suspected) throws CompileException {
        final Token token = read();
        final int result = indexOf(suspected, token.getType());
        if (result == -1) {
            throw new CompileException("One of " + join(suspected, ", ") + " expected instead of \"" + token.getType() + "\"", token.getLocation());
        }
        return token;
    }

    public boolean peekRead(String expected) {
        if (expected.equals(peek().getText())) {
            read();
            return true;
        }
        return false;
    }

    public boolean peekRead(TokenType expected) {
        if (expected.equals(peek().getType())) {
            read();
            return true;
        }
        return false;
    }

    public int peekRead(String... suspected) {
        final int result = indexOf(suspected, peek().getText());
        if (result != -1) read();
        return result;
    }

    public int peekRead(TokenType... suspected) {
        final int result = indexOf(suspected, peek().getType());
        if (result != -1) read();
        return result;
    }

    private Token produce() {
        while (true) {
            Token token = scanner.produce();
            switch (token.getType()) {
                case WHITE_SPACE:
                case SINGLE_LINE_COMMENT:
                case MULTI_LINE_COMMENT:
                    break;
                default:
                    return token;
            }
        }
    }

    private static int indexOf(String[] a, String o) {
        for (int i = 0; i < a.length; i++) {
            if (a[i].equals(o)) return i;
        }
        return -1;
    }

    private static int indexOf(TokenType[] a, TokenType o) {
        for (int i = 0; i < a.length; i++) {
            if (a[i].equals(o)) return i;
        }
        return -1;
    }

    private static String join(Object[] a, String delimiter) {
        if (a == null) return "(null)";

        if (a.length == 0) return "(zero length array)";

        StringBuffer sb = new StringBuffer().append(a[0]);
        for (int i = 1; i < a.length; i++) {
            sb.append(delimiter).append(a[i]);
        }
        return sb.toString();
    }

    private static String join(String[] a, String delimiter) {
        if (a == null) return "(null)";

        if (a.length == 0) return "(zero length array)";

        StringBuffer sb = new StringBuffer().append("\"").append(a[0]).append("\"");
        for (int i = 1; i < a.length; i++) {
            sb.append(delimiter).append("\"").append(a[1]).append("\"");
        }
        return sb.toString();
    }
}
