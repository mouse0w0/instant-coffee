package com.github.mouse0w0.instantcoffee;

import org.junit.jupiter.api.Test;

public class HelloWorldTest {
    @Test
    public void test() {
        Utils.validate(HelloWorld.class);
    }

    private static class HelloWorld {
        public static void main(String[] args) {
            System.out.println("Hello World!");
        }
    }
}
