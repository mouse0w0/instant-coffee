package com.github.mouse0w0.instantcoffee;

import java.io.Serializable;
import java.util.List;

public class GenericTypeTest {
    public static void main(String[] args) {
        Utils.validate(GenericType.class);
    }

    private static class GenericType<T, U extends Number, V extends Serializable> {
        private T t;
        private U u;
        private V v;

        private List<T> TList;
        private List<? extends T> extendsTList;
        private List<? super T> superTList;

        private List<U> UList;
        private List<? extends U> extendsUList;
        private List<? super U> superUList;

        private List<V> VList;
        private List<? extends V> extendsVList;
        private List<? super V> superVList;

        public void m(T t, U u, V v) {
        }
    }
}
