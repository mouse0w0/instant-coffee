package com.github.mouse0w0.instantcoffee.model.statement;

import com.github.mouse0w0.instantcoffee.Location;

import java.util.List;

public class Block extends Statement {
    public List<Statement> statements;

    public Block(Location location, List<Statement> statements) {
        super(location);
        this.statements = statements;
    }
}
