package com.github.mouse0w0.instantcoffee;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@AnnotationVisible.Visible
@AnnotationVisible.Invisible
public class AnnotationVisible {
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Visible {

    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.CLASS)
    public @interface Invisible {

    }

    public static class Main {
        public static void main(String[] args) {
            String decompiled = Utils.decompile(AnnotationVisible.class);
            System.out.println(decompiled);
            byte[] recompiled = Utils.compile(decompiled);
            String derecompiled = Utils.decompile(recompiled);
            System.out.println(derecompiled);
            System.out.println(decompiled.equals(derecompiled));
        }
    }
}
