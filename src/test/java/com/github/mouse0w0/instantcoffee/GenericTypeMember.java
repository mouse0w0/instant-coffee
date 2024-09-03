package com.github.mouse0w0.instantcoffee;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class GenericTypeMember {
    private List listRaw;
    private List<?> listWildcard;
    private List<String> listString;
    private List<? extends Number> listExtendsNumber;
    private List<? super Number> listSuperNumber;
    private List<? extends Serializable> listExtendsSerializable;
    private List<? super Serializable> listSuperSerializable;

    private Map mapRaw;
    private Map<?, ?> mapWildcard;
    private Map<Object, String> mapString;
    private Map<Object, ? extends Number> mapExtendsNumber;
    private Map<Object, ? super Number> mapSuperNumber;
    private Map<Object, ? extends Serializable> mapExtendsSerializable;
    private Map<Object, ? super Serializable> mapSuperSerializable;

    private static void method0(Consumer<?> consumer, Supplier<?> supplier) {
    }

    private static void method1(Consumer<String> consumer, Supplier<String> supplier) {
        consumer.accept(supplier.get());
    }

    private static void method2(Consumer<? super Number> consumer, Supplier<? extends Number> supplier) {
        consumer.accept(supplier.get());
    }

    private static <T> void method3(Consumer<? super T> consumer, Supplier<? extends T> supplier) {
        consumer.accept(supplier.get());
    }

    private static <T extends Number> void method4(Consumer<? super T> consumer, Supplier<? extends T> supplier) {
        consumer.accept(supplier.get());
    }

    private static Object method5(Function<?, ?> function) {
        return null;
    }

    private static Number method6(Function<String, Number> function) {
        return null;
    }

    private static Number method7(Function<? super String, ? extends Number> function) {
        return null;
    }

    private static <T, R> R method8(Function<T, R> function) {
        return null;
    }

    private static <T, R> R method9(Function<? super T, ? extends R> function) {
        return null;
    }

    private static <T extends String, R extends Number> R method10(Function<? super T, ? extends R> function) {
        return null;
    }

    private static <T, U extends Interface1, V extends Interface1 & Interface2> void method11(T t, U u, V v) {
    }

    interface Interface1 {
    }

    interface Interface2 {
    }

    public static class Main {
        public static void main(String[] args) {
            Utils.check(GenericTypeMember.class);
        }
    }
}
