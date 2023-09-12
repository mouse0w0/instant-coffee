package com.github.mouse0w0.instantcoffee;

public class Token {
    private final Location location;
    private final TokenType type;
    private final String text;

    public Token(Location location, TokenType type, String text) {
        this.location = location;
        this.type = type;
        this.text = text;
    }

    public Location getLocation() {
        return location;
    }

    public TokenType getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return text;
    }
}
