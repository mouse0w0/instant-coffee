package com.github.mouse0w0.instantcoffee;

public class StringScanner {
    public static final char EOS = '\0';

    private final String string;
    private int pos;

    public StringScanner(String string) {
        this.string = string;
    }

    public String getString() {
        return string;
    }

    public String substring(int start) {
        return string.substring(start);
    }

    public String substring(int start, int end) {
        return string.substring(start, end);
    }

    public int pos() {
        return pos;
    }

    public boolean hasNext() {
        return pos < string.length();
    }

    public void next() {
        pos++;
    }

    public char peek() {
        return hasNext() ? string.charAt(pos) : EOS;
    }

    public boolean peek(char c) {
        return peek() == c;
    }

    public char read() {
        return hasNext() ? string.charAt(pos++) : EOS;
    }

    public boolean peekRead(char c) {
        if (peek(c)) {
            pos++;
            return true;
        }
        return false;
    }
}
