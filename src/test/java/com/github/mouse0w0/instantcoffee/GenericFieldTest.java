package com.github.mouse0w0.instantcoffee;

import org.junit.jupiter.api.Test;

import java.util.List;

public class GenericFieldTest {

    @Test
    public void test() {
        Utils.validate(GenericField.class);
    }

    private static class GenericField<T, U extends A, V extends B> {
        private T t;
        private U u;
        private V v;

        private List<T> TList;
        private List<? extends T> extendsTList;
        private List<? super T> superTList;
    }

    private static class A {
    }

    private interface B {
    }
}