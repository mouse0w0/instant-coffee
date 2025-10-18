package com.github.mouse0w0.instantcoffee.model.statement;

import com.github.mouse0w0.instantcoffee.Location;

import java.util.List;

public class SwitchInsn extends BaseInsn {
    public List<SwitchCase> cases;
    public String dflt;

    public SwitchInsn(Location location, List<SwitchCase> cases, String dflt) {
        super(location, null);
        this.cases = cases;
        this.dflt = dflt;
    }
}
