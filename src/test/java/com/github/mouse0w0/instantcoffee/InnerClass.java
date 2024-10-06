package com.github.mouse0w0.instantcoffee;

public class InnerClass {
    public class MemberInnerClass {}

    public static class StaticInnerClass {}

    public interface InterfaceInnerClass {}

    public abstract class AbstractInnerClass {}

    public @interface AnnotationInnerClass {}

    public enum EnumInnerClass {}

    public static class Main {
        public static void main(String[] args) {
            Utils.check(InnerClass.class);
        }
    }
}
