package com.github.mouse0w0.instantcoffee;

public @interface AnnotationDeclaration {
    byte b();

    short s();

    int i();

    long l();

    float f();

    double d();

    boolean z();

    char c();

    String str();

    Class<?> clazz();

    Class<?>[] clazzArray();

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
