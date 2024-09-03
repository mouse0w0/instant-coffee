package com.github.mouse0w0.instantcoffee;

public class Hierarchy {
    public static class Parent {
    }

    public interface Interface1 {
    }

    public interface Interface2 {
    }

    public static class Child extends Parent implements Interface1, Interface2 {
    }

    public static class Main {
        public static void main(String[] args) {
            Utils.check(Child.class);
        }
    }
}
