package com.github.mouse0w0.instantcoffee;

import org.junit.jupiter.api.Test;

public class OuterClassTest {
    @Test
    public void testLocalInnerClass() {
        Utils.validateInnerClassIgnoreTextified(TestClass.class, "1LocalClass");
    }

    @Test
    public void testAnonymousInMethod() {
        Utils.validateInnerClassIgnoreTextified(TestClass.class, "1");
    }

    @Test
    public void testAnonymousInField() {
        Utils.validateInnerClassIgnoreTextified(TestClass.class, "2");
    }

    public static class TestClass {
        private final Runnable fieldAnonymous = new Runnable() {
            @Override
            public void run() {}
        };

        public void test() {
            class LocalClass {}
            Runnable methodAnonymous = new Runnable() {
                @Override
                public void run() {}
            };
            new LocalClass();
            methodAnonymous.run();
            fieldAnonymous.run();
        }
    }
}
