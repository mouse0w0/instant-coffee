package com.github.mouse0w0.instantcoffee;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BlockTest {
    @Test
    public void test() {
        String input = Utils.readString(BlockTest.class.getResourceAsStream("/BlockTest_input.txt"));
        String expected = Utils.readString(BlockTest.class.getResourceAsStream("/BlockTest_expected.txt"));
        String actual = Utils.decompile(Utils.compile(input));
        Assertions.assertEquals(expected, actual);
    }
}
