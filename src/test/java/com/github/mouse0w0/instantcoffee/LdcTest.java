package com.github.mouse0w0.instantcoffee;

import org.junit.jupiter.api.Test;

public class LdcTest {
    @Test
    public void test() {
        Utils.check(LdcTestClass.class);
    }

    private static class LdcTestClass {
        private void a() {
            // TODO: type signature
            Class a = String.class;
            Class b = String[].class;
            Class c = String[][][].class;
            Class d = int.class;
            Class e = void.class;
            Class f = int[].class;
            Class g = int[][][].class;
        }
    }
}
