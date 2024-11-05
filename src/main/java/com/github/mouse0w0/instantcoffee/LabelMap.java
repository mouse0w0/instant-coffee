package com.github.mouse0w0.instantcoffee;

import org.objectweb.asm.Label;

import java.util.Map;

public final class LabelMap {
    private final LabelMap parent;
    private final Map<String, Label> map;

    public LabelMap(LabelMap parent, Map<String, Label> map) {
        this.parent = parent;
        this.map = map;
    }

    public Label get(String name) {
        for (LabelMap curr = this; curr != null; curr = curr.parent) {
            Label label = curr.map.get(name);
            if (label != null) {
                return label;
            }
        }
        return null;
    }
}
