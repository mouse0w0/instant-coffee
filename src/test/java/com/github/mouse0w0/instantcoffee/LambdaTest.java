package com.github.mouse0w0.instantcoffee;

import org.junit.jupiter.api.Test;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class LambdaTest {
    @Test
    public void test() {
        Utils.validate(Lambda.class);
    }

    private static class Lambda {
        public static void method() {
            Consumer consumer = o -> System.out.println(o);
            Function function = o -> o.toString();
            Predicate predicate = o -> true;
            Supplier supplier = () -> new Object();
        }
    }
}
