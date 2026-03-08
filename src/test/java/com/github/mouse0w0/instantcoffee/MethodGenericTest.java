package com.github.mouse0w0.instantcoffee;

public class MethodGenericTest {

    public <T> MethodGenericTest() {

    }

    public <T, U, V, R, EX extends Exception, RE extends RuntimeException> R test(T t, U u, V v) throws EX, RE {
        return null;
    }
}
