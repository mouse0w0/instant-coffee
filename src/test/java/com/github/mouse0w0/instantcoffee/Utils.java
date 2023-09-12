package com.github.mouse0w0.instantcoffee;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.*;

public class Utils {
    public static byte[] compile(String s) {
        return new Compiler().compile(new Parser(new TokenStream(new Scanner(new StringReader(s)))).parseClassDeclaration()).toByteArray();
    }

    public static String decompile(Class<?> clazz) {
        try (InputStream input = openStream(clazz)) {
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

    public static String textify(Class<?> clazz) {
        try (InputStream inputStream = openStream(clazz)) {
            StringWriter sw = new StringWriter();
            new ClassReader(inputStream).accept(new TraceClassVisitor(null, new Textifier(), new PrintWriter(sw)), ClassReader.SKIP_FRAMES);
            return sw.toString();
        } catch (IOException e) {
            throw new UncheckedIOException(e.getMessage(), e);
        }
    }

    public static String textify(byte[] bytes) {
        StringWriter sw = new StringWriter();
        new ClassReader(bytes).accept(new TraceClassVisitor(null, new Textifier(), new PrintWriter(sw)), ClassReader.SKIP_FRAMES);
        return sw.toString();
    }

    private static InputStream openStream(Class<?> clazz) {
        String classQualifiedName = clazz.getName();
        int index = classQualifiedName.lastIndexOf('.');
        String className = index != -1 ? classQualifiedName.substring(index + 1) : classQualifiedName;
        return clazz.getResourceAsStream(className + ".class");
    }
}
