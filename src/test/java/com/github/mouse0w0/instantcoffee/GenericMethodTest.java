package com.github.mouse0w0.instantcoffee;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GenericMethodTest {

    @Test
    public void test() {
        Utils.validate(GenericMethod.class);
    }

    private static class GenericMethod<T, U extends A, V extends B & C> {

        private static <T, X extends D, Y extends D & E> void method1(T t, X x, Y y) {
        }

        private static <T extends Number & Comparable<T>, X, Y extends Runnable & Cloneable> void method2(T t, X x, Y y) {
        }

        private static <T extends Enum<T>> T method3(T[] values, int index) {
            return values[index];
        }

        private static <T> T[] method4(T[] array) {
            return array;
        }

        private static <T> T method5(T... args) {
            return args[0];
        }

        private static <K, V> Map<K, List<V>> method6(Map<K, List<V>> map) {
            return map;
        }

        private static <T> Supplier<List<T>> method7(List<T> list) {
            return () -> list;
        }

        private static <T> void method8(Consumer<Supplier<T>> consumer) {
        }

        public <X> GenericMethod(X x) {
        }

        public <X extends Number> GenericMethod(X x, String name) {
        }

        public <X extends A & B, Y extends D & E> GenericMethod(X x, Y y) {
        }

        public void method11(T t) {
        }

        public void method12(U u, V v) {
        }

        public <R> R method13(T t, R r) {
            return r;
        }

        public <R extends U> R method14(R r) {
            return r;
        }

        public void method15(Consumer<? super T> consumer, Supplier<? extends V> supplier) {
            consumer.accept(null);
            supplier.get();
        }

        public <R> Map<T, List<R>> method16(R r) {
            return null;
        }
    }

    private static class A {
    }

    private interface B {
    }

    private interface C {
    }

    private interface D {
    }

    private interface E {
    }
}