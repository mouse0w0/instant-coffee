package com.github.mouse0w0.instantcoffee;

import org.junit.jupiter.api.Test;

public class StaticBlockTest {
    @Test
    public void test() {
        Utils.validate(StaticBlock.class);
    }

    private static class StaticBlock {
        static {
            System.out.println();
        }
    }
}
