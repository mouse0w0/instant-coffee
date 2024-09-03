package com.github.mouse0w0.instantcoffee;

public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello World!");
    }

    public static class Main {
        public static void main(String[] args) {
            Utils.check(HelloWorld.class);
        }
    }
}
