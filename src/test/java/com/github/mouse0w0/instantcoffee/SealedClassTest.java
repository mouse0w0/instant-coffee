package com.github.mouse0w0.instantcoffee;

import org.junit.jupiter.api.Test;

public class SealedClassTest {
    @Test
    public void test() {
        Utils.validate(SealedClass.class);
    }

    public static sealed class SealedClass {

    }

    public static non-sealed class NonSealedClass extends SealedClass {

    }

    public static final class FinalClass extends SealedClass {

    }
}
