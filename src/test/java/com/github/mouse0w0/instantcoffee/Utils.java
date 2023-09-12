package com.github.mouse0w0.instantcoffee;

import org.objectweb.asm.ClassReader;

import java.io.*;

public class Utils {
    public static String decompile(Class<?> clazz) {
        String classQualifiedName = clazz.getName();
        int index = classQualifiedName.lastIndexOf('.');
        String className = index != -1 ? classQualifiedName.substring(index + 1) : classQualifiedName;
        try (InputStream input = clazz.getResourceAsStream(className + ".class")) {
            StringWriter sw = new StringWriter();
            Unparser.unparse(Decompiler.decompile(new ClassReader(input)), sw);
            return sw.toString();
        } catch (IOException e) {
            throw new UncheckedIOException(e.getMessage(), e);
        }
    }

    public static String decompile(byte[] bytes) {
        StringWriter sw = new StringWriter();
        Unparser.unparse(Decompiler.decompile(new ClassReader(bytes)), sw);
        return sw.toString();
    }

    public static byte[] compile(String s) {
        return new Compiler().compile(new Parser(new TokenStream(new Scanner("<unknown>", new StringReader(s)))).parseClassDeclaration()).toByteArray();
    }
}
