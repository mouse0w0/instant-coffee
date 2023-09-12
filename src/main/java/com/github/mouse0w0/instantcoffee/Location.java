package com.github.mouse0w0.instantcoffee;

public class Location {
    public static final Location UNKNOWN = new Location("<unknown>", -1, -1);

    private final String fileName;
    private final int line;
    private final int column;

    public Location(String fileName, int line, int column) {
        this.fileName = fileName;
        this.line = line;
        this.column = column;
    }

    public String getFileName() {
        return fileName;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public String toString() {
        return fileName + ':' + line + ":" + column;
    }
}
