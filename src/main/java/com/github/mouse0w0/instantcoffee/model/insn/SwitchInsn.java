package com.github.mouse0w0.instantcoffee.model.insn;

import com.github.mouse0w0.instantcoffee.Location;
import com.github.mouse0w0.instantcoffee.model.IntegerLiteral;

public class SwitchInsn extends BaseInsn {
    public IntegerLiteral[] keys;
    public String[] labels;
    public String defaultLabel;

    public SwitchInsn(Location location, IntegerLiteral[] keys, String[] labels, String defaultLabel) {
        super(location, null);
        this.keys = keys;
        this.labels = labels;
        this.defaultLabel = defaultLabel;
    }
}
