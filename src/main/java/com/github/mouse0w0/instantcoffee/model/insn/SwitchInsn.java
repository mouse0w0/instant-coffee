package com.github.mouse0w0.instantcoffee.model.insn;

import com.github.mouse0w0.instantcoffee.Location;

public class SwitchInsn extends BaseInsn {
    public SwitchCase[] cases;
    public String dflt;

    public SwitchInsn(Location location, SwitchCase[] cases, String dflt) {
        super(location, null);
        this.cases = cases;
        this.dflt = dflt;
    }
}
