package com.github.mouse0w0.instantcoffee;

import org.junit.jupiter.api.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class AnnotationVisibleTest {
    @Test
    public void test() {
        Utils.validate(AnnotationVisible.class);
    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    private @interface Visible {
    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.CLASS)
    private @interface Invisible {
    }

    @AnnotationVisibleTest.Visible
    @AnnotationVisibleTest.Invisible
    private static class AnnotationVisible {
    }
}
