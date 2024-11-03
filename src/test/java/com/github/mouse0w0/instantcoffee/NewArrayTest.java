package com.github.mouse0w0.instantcoffee;

import org.junit.jupiter.api.Test;

public class NewArrayTest {
    @Test
    public void test() {
        Utils.validate(NewArray.class);
    }

    private static class NewArray {
        public void method() {
            boolean[] a = new boolean[100];
            char[] b = new char[100];
            float[] c = new float[100];
            double[] d = new double[100];
            byte[] e = new byte[100];
            short[] f = new short[100];
            int[] g = new int[100];
            long[] h = new long[100];
        }
    }
}
