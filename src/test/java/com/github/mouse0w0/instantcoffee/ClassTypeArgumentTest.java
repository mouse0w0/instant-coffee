package com.github.mouse0w0.instantcoffee;

import org.junit.jupiter.api.Test;

public class ClassTypeArgumentTest {
    @Test
    public void test() {
        Utils.validate(ClassTypeArgument.class, false, true);
    }

    // signature Lcom/github/mouse0w0/instantcoffee/ClassTypeArgumentTest$A<Lcom/github/mouse0w0/instantcoffee/ClassTypeArgumentTest$C;>;Lcom/github/mouse0w0/instantcoffee/ClassTypeArgumentTest$B<Lcom/github/mouse0w0/instantcoffee/ClassTypeArgumentTest$C;>;
    public static class ClassTypeArgument extends A<C> implements B<C> {
    }

    public static class A<T> {
    }

    public interface B<T> {
    }

    public interface C {
    }
}
