package com.github.mouse0w0.instantcoffee;

import org.junit.jupiter.api.Test;

public class AnnotationDeclarationTest {
    @Test
    public void test() {
        Utils.validate(AnnotationDeclaration.class);
    }

    private enum A {
        A, B, C
    }

    private @interface B {
        int value();
    }

    private @interface AnnotationDeclaration {
        String unassign();

        byte b() default 127;

        short s() default 32767;

        int i() default 2147483647;

        long l() default 9223372036854775807L;

        float f() default 1.0F;

        double d() default 1.0D;

        boolean z() default true;

        char c() default 'c';

        String string() default "string";

        A _enum() default A.A;

        B anno() default @B(111);

        Class _class() default Object.class;

        Class[] classes() default {Integer.class, Long.class, Float.class, Double.class};
    }
}
