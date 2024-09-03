package com.github.mouse0w0.instantcoffee;

public class StaticInitializer {
    static {
        System.out.println();
    }

    public static class Main {
        public static void main(String[] args) {
            Utils.check(StaticInitializer.class);
        }
    }
}
