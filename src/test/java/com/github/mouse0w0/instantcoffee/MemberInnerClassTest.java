package com.github.mouse0w0.instantcoffee;

import org.junit.jupiter.api.Test;

public class MemberInnerClassTest {
    @Test
    public void test() {
        Utils.validate(Outer.class);
    }

    public static class Outer {
        public class InnerClass {
        }

        public final class FinalInnerClass {
        }

        public static class StaticInnerClass {}

        public static final class StaticFinalInnerClass {}

        public interface InterfaceInnerClass {}

        public abstract class AbstractInnerClass {}

        public @interface AnnotationInnerClass {}

        public enum EnumInnerClass {}
    }
}
