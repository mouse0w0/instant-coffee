package com.github.mouse0w0.instantcoffee;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Utils {
    public static void validate(Class<?> clazz) {
        validate(clazz, false);
    }

    public static void validate(Class<?> clazz, boolean print) {
        String decompiledRaw = decompile(clazz);
        String textifiedRaw = textify(clazz);

        if (print) {
            writeString(Paths.get(clazz.getSimpleName() + "_decompiled_raw.txt"), decompiledRaw);
            writeString(Paths.get(clazz.getSimpleName() + "_textified_raw.txt"), textifiedRaw);
        }

        byte[] recompiled = compile(decompiledRaw);

        String decompiledNew = decompile(recompiled);
        String textifiedNew = textify(recompiled);

        if (print) {
            writeString(Paths.get(clazz.getSimpleName() + "_decompiled_new.txt"), decompiledNew);
            writeString(Paths.get(clazz.getSimpleName() + "_textified_new.txt"), textifiedNew);
        }

        if (decompiledRaw.equals(decompiledNew) && textifiedRaw.equals(textifiedNew)) {
            return;
        }

        throw new AssertionError();
    }

    public static String readString(InputStream inputStream) {
        return readString(new InputStreamReader(inputStream));
    }

    public static String readString(Reader reader) {
        StringBuilder sb = new StringBuilder();
        char[] buf = new char[8192];
        int n;
        try {
            while ((n = reader.read(buf)) != -1) {
                sb.append(buf, 0, n);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return sb.toString();
    }

    public static void writeString(Path path, String string, OpenOption... options) {
        try (BufferedWriter writer = Files.newBufferedWriter(path, options)) {
            writer.write(string);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static byte[] compile(String s) {
        return new Compiler().compile(new Parser(s).parseClassDeclaration()).toByteArray();
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
