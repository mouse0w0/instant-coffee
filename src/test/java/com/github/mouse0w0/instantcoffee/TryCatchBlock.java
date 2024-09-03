package com.github.mouse0w0.instantcoffee;

public class TryCatchBlock {
    public static void tryCatchBlock() {
        try {
            System.out.println("try");
        } catch (RuntimeException e) {
            System.out.println("catch");
        } finally {
            System.out.println("finally");
        }
    }

    public static class Main {
        public static void main(String[] args) {
            Utils.check(TryCatchBlock.class);
        }
    }
}