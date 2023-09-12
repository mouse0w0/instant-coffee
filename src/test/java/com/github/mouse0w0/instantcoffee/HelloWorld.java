package com.github.mouse0w0.instantcoffee;

public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello World!");
    }

    public static class Main {
        public static void main(String[] args) {
            String decompiled = Utils.decompile(HelloWorld.class);
            System.out.println(decompiled);
            byte[] recompiled = Utils.compile(decompiled);
            String derecompiled = Utils.decompile(recompiled);
            System.out.println(derecompiled);
            System.out.println(decompiled.equals(derecompiled));
        }
    }
}
