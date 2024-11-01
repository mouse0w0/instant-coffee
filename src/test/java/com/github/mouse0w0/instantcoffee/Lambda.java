package com.github.mouse0w0.instantcoffee;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Lambda {
    public static void method() {
        Consumer consumer = o -> System.out.println(o);
        Function function = o -> o.toString();
        Predicate predicate = o -> true;
        Supplier supplier = () -> new Object();
    }

    public static class Main {
        public static void main(String[] args) {
            Utils.check(Lambda.class);
        }
    }
}
