package com.github.mouse0w0.instantcoffee;

public @interface AnnotationDeclaration {
    byte b() default 127;

    short s() default 32767;

    int i() default 2147483647;

    long l() default 9223372036854775807L;

    float f();

    double d();

    boolean z();

    char c();

    String str();

    Enum enumValue() default Enum.A;

    Anno anno() default @Anno(111);

    Class<?> clazz() default Object.class;

    Class<?>[] clazzArray();

    enum Enum {
        A, B, C
    }

    @interface Anno {
        int value();
    }

    class Main {
        public static void main(String[] args) {
            String decompiled = Utils.decompile(AnnotationDeclaration.class);
            System.out.println(decompiled);
            byte[] recompiled = Utils.compile(decompiled);
            String derecompiled = Utils.decompile(recompiled);
            System.out.println(derecompiled);
            System.out.println(decompiled.equals(derecompiled));
        }
    }
}
