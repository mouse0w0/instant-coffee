package com.github.mouse0w0.instantcoffee;

import org.junit.jupiter.api.Test;

public class InvokeInterfaceTest {
    @Test
    public void test() {
        Utils.validate(InvokeInterface.class);
    }

    public static final class InvokeInterface {
        private void test() {
            Comparable a = "a";
            Comparable b = "b";
            a.compareTo(b);
        }
    }
}
