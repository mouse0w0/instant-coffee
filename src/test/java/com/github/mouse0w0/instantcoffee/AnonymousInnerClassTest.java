package com.github.mouse0w0.instantcoffee;

import org.junit.jupiter.api.Test;

public class AnonymousInnerClassTest {
    @Test
    public void test() {
        Utils.validate(Outer.class);
    }

    public static class Outer {
        public void test() {
            new Runnable() {
                @Override
                public void run() {
                }
            };
        }
    }
}
