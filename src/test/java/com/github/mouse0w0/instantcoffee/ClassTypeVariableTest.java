package com.github.mouse0w0.instantcoffee;

import org.junit.jupiter.api.Test;

public class ClassTypeVariableTest {
    @Test
    public void test() {
        Utils.validate(ClassTypeVariable.class, true, false);
    }

    // signature <T:Ljava/lang/Object;U:Ljava/lang/Object;>Lcom/github/mouse0w0/instantcoffee/GenericClassDeclarationTest$A<TT;>;Lcom/github/mouse0w0/instantcoffee/GenericClassDeclarationTest$B<TU;>;
    public static class ClassTypeVariable<T, U> extends A<T> implements B<U> {
    }

    public static class A<T> {
    }

    public interface B<T> {
    }
}
