package com.github.mouse0w0.instantcoffee;

import org.junit.jupiter.api.Test;

public class TryCatchBlockTest {
    @Test
    public void test() {
        Utils.validate(TryCatchBlock.class);
    }

    private static class TryCatchBlock {
        public static void method() {
            try {
                System.out.println("try");
            } catch (RuntimeException e) {
                System.out.println("catch");
            } finally {
                System.out.println("finally");
            }
        }
    }
}