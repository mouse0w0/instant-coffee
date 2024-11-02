package com.github.mouse0w0.instantcoffee;

import org.junit.jupiter.api.Test;

public class HierarchyTest {
    @Test
    public void test() {
        Utils.validate(Child.class);
    }

    public static class Parent {
    }

    public interface Interface1 {
    }

    public interface Interface2 {
    }

    public static class Child extends Parent implements Interface1, Interface2 {
    }
}
