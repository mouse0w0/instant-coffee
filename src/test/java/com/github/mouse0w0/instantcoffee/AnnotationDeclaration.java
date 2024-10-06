package com.github.mouse0w0.instantcoffee;

public @interface AnnotationDeclaration {
    byte b() default 127;

    short s() default 32767;

    int i() default 2147483647;

    long l() default 9223372036854775807L;

    float f() default 1.0f;

    double d() default 1.0;

    boolean z() default true;

    char c() default 'c';

    String string() default "string";

    Enum enumValue() default Enum.A;

    Anno anno() default @Anno(111);

    Class clazz() default Object.class;

    Class[] clazzArray() default {Integer.class, Long.class, Float.class, Double.class};

    enum Enum {
        A, B, C
    }

    @interface Anno {
        int value();
    }

    class Main {
        public static void main(String[] args) {
            Utils.check(AnnotationDeclaration.class);
        }
    }
}
