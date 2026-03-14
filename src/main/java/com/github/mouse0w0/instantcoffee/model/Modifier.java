package com.github.mouse0w0.instantcoffee.model;

import com.github.mouse0w0.instantcoffee.Location;

import java.util.List;

public final class Modifier extends Located {
    public String keyword;

    public Modifier(Location location, String keyword) {
        super(location);
        this.keyword = keyword;
    }

    public static boolean hasModifier(List<Modifier> modifiers, String keyword) {
        for (Modifier modifier : modifiers) {
            if (modifier.keyword.equals(keyword)) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasModifier(List<Modifier> modifiers, String... keywords) {
        for (Modifier modifier : modifiers) {
            for (String keyword : keywords) {
                if (modifier.keyword.equals(keyword)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return keyword;
    }
}
