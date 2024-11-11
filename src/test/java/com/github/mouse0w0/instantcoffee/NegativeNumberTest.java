package com.github.mouse0w0.instantcoffee;

import org.junit.jupiter.api.Test;

public class NegativeNumberTest {
    @Test
    public void test() {
        Utils.validate(NegativeNumber.class);
    }

    private static class NegativeNumber {
        public void method() {
            int a = -1;
            int b = -2;
            int c = -2147483648;

            long d = -1;
            long e = -2;
            long f = -2147483648;
            long h = -9223372036854775808L;

            float i = -1.0f;
            float j = -2.0f;
            float k = -3.4028235E38f;

            double l = -1.0;
            double m = -2.0;
            double n = -1.7976931348623157E308;
        }
    }
}
