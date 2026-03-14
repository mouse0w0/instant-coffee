package com.github.mouse0w0.instantcoffee;

import org.junit.jupiter.api.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class RecordAnnotationTest {
    @Test
    public void test() {
        Utils.validateIgnoreTextified(RecordAnnotation.class);
    }

    @Target(ElementType.RECORD_COMPONENT)
    @Retention(RetentionPolicy.RUNTIME)
    private @interface RecordComponentAnnotation {
        String value() default "";
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    private @interface FieldAnnotation {
        String value() default "";
    }

    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.RUNTIME)
    private @interface ParameterAnnotation {
        String value() default "";
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    private @interface MethodAnnotation {
        String value() default "";
    }

    private record RecordAnnotation(
            @RecordComponentAnnotation("component")
            @FieldAnnotation("field")
            @ParameterAnnotation("parameter")
            @MethodAnnotation("method")
            String name
    ) {
    }
}
