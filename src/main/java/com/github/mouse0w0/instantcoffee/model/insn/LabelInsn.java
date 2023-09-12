package com.github.mouse0w0.instantcoffee.model.insn;

import com.github.mouse0w0.instantcoffee.Location;

public class LabelInsn extends BaseInsn {
    public String name;

    public LabelInsn(Location location, String name) {
        super(location, null);
        this.name = name;
    }
}
