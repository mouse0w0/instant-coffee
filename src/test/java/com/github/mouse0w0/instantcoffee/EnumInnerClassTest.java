package com.github.mouse0w0.instantcoffee;

import org.junit.jupiter.api.Test;

public class EnumInnerClassTest {
    @Test
    public void test() {
        Utils.validateIgnoreTextified(OuterEnum.class);
    }

    public enum OuterEnum {
        A {
            @Override
            public void test() {
                int i = 1;
            }
        };

        public void test() {
            int i = 0;
        }
    }
}
