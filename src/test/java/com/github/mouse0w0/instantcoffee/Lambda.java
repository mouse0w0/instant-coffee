package com.github.mouse0w0.instantcoffee;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Lambda {
    public static void method() {
        Consumer<?> consumer = o -> System.out.println(o);
        Function<?, ?> function = o -> o.toString();
        Predicate<?> predicate = o -> true;
        Supplier<?> supplier = () -> new Object();
    }

    public static class Main {
        public static void main(String[] args) {
            String decompiled = Utils.decompile(Lambda.class);
            System.out.println(decompiled);
            byte[] recompiled = Utils.compile(decompiled);
            String derecompiled = Utils.decompile(recompiled);
            System.out.println(derecompiled);
            System.out.println(decompiled.equals(derecompiled));
        }
    }
}
