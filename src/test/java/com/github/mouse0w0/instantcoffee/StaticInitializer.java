package com.github.mouse0w0.instantcoffee;

public class StaticInitializer {
    static {
        System.out.println();
    }

    public static class Main {
        public static void main(String[] args) {
            String decompiled = Utils.decompile(StaticInitializer.class);
            System.out.println(decompiled);
            byte[] recompiled = Utils.compile(decompiled);
            String derecompiled = Utils.decompile(recompiled);
            System.out.println(derecompiled);
            System.out.println(decompiled.equals(derecompiled));
        }
    }
}
