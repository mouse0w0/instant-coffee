package com.github.mouse0w0.instantcoffee;

import org.junit.jupiter.api.Test;

public class RecordTest {
    @Test
    public void test() {
        Utils.validateIgnoreTextified(Record.class);
    }

    private record Record(int x, int y) {
    }
}
