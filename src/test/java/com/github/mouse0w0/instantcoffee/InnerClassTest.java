package com.github.mouse0w0.instantcoffee;

import org.junit.jupiter.api.Test;

public class InnerClassTest {
    @Test
    public void test() {
        Utils.validate(InnerClass.class);
    }

    public static class InnerClass {
        public class MemberInnerClass {}

        public final class MemberFinalInnerClass {}

        public static class StaticInnerClass {}

        public static final class StaticFinalInnerClass {}

        public interface InterfaceInnerClass {}

        public abstract class AbstractInnerClass {}

        public @interface AnnotationInnerClass {}

        public enum EnumInnerClass {}
    }
}
