package com.github.mouse0w0.instantcoffee;

import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class WildcardTest {
    @Test
    public void test() {
        Utils.validate(Wildcard.class);
    }

    private static class Wildcard {
        private List<?> listWildcard;
        private List<String> listString;
        private List<? extends Number> listExtendsNumber;
        private List<? super Number> listSuperNumber;
        private List<? extends Serializable> listExtendsSerializable;
        private List<? super Serializable> listSuperSerializable;

        private Map<?, ?> mapWildcard;
        private Map<Object, String> mapString;
        private Map<Object, ? extends Number> mapExtendsNumber;
        private Map<Object, ? super Number> mapSuperNumber;
        private Map<Object, ? extends Serializable> mapExtendsSerializable;
        private Map<Object, ? super Serializable> mapSuperSerializable;

        private static void method1(Consumer<String> consumer, Supplier<String> supplier) {
        }

        private static void method2(Consumer<? super Number> consumer, Supplier<? extends Number> supplier) {
        }

        private static Number method6(Function<String, Number> function) {
            return null;
        }

        private static Number method7(Function<? super String, ? extends Number> function) {
            return null;
        }
    }
}