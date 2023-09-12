package com.github.mouse0w0.instantcoffee.model.insn;

import com.github.mouse0w0.instantcoffee.Location;
import com.github.mouse0w0.instantcoffee.model.IntegerLiteral;

public class LineNumberInsn extends BaseInsn {
    public IntegerLiteral line;
    public String label;

    public LineNumberInsn(Location location, IntegerLiteral line, String label) {
        super(location, null);
        this.line = line;
        this.label = label;
    }
}
