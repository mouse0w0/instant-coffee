package com.github.mouse0w0.instantcoffee;

import org.junit.jupiter.api.Test;

public class ArrayInternalNameTest {
    @Test
    public void test() {
        Utils.validate(ArrayInternalName.class);
    }

    private static final class ArrayInternalName {
        int[] ints = new int[0];
        String[] strings = new String[0];

        private void test() {
            int[] clone = ints.clone();
            String[] clone2 = strings.clone();
        }
    }
}
