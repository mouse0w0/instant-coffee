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
        validate(clazz.getName(), clazz.getSimpleName(), false, false);
    }

    public static void validate(Class<?> clazz, boolean ignoredTextified, boolean print) {
        validate(clazz.getName(), clazz.getSimpleName(), ignoredTextified, print);
    }

    public static void validateIgnoreTextified(Class<?> clazz) {
        validate(clazz.getName(), clazz.getSimpleName(), true, false);
    }

    public static void validateInnerClass(Class<?> outerClass, String innerClassName) {
        validate(outerClass.getName() + "$" + innerClassName,
                outerClass.getSimpleName() + "$" + innerClassName, false, false);
    }

    public static void validateInnerClass(Class<?> outerClass, String innerClassName, boolean ignoredTextified, boolean print) {
        validate(outerClass.getName() + "$" + innerClassName,
                outerClass.getSimpleName() + "$" + innerClassName, ignoredTextified, print);
    }

    public static void validateInnerClassIgnoreTextified(Class<?> outerClass, String innerClassName) {
        validate(outerClass.getName() + "$" + innerClassName,
                outerClass.getSimpleName() + "$" + innerClassName, true, false);
    }

    public static void validate(String className, String simpleName, boolean ignoredTextified, boolean print) {
        byte[] classBytes = readClassBytes(className);

        String ic = decompile(classBytes);
        String textify = textify(classBytes);

        if (print) {
            writeString(Paths.get(simpleName + ".raw.ic"), ic);
            writeString(Paths.get(simpleName + ".raw.txt"), textify);
        }

        byte[] recompiled = compile(ic);

        String recompiledIc = decompile(recompiled);
        String recompiledTextify = textify(recompiled);

        if (print) {
            writeString(Paths.get(simpleName + ".recompiled.ic"), recompiledIc);
            writeString(Paths.get(simpleName + ".recompiled.txt"), recompiledTextify);
        }

        if (ic.equals(recompiledIc) && (ignoredTextified || textify.equals(recompiledTextify))) {
            return;
        }

        throw new AssertionError("Validation failed for: " + className);
    }

    private static byte[] readClassBytes(String className) {
        String resourcePath = className.replace('.', '/') + ".class";
        try (InputStream input = Utils.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (input == null) {
                throw new IllegalArgumentException("Class not found: " + className);
            }
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] data = new byte[8192];
            int nRead;
            while ((nRead = input.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            return buffer.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e.getMessage(), e);
        }
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

    public static String decompile(byte[] bytes) {
        StringWriter sw = new StringWriter();
        Unparser.unparse(Decompiler.decompile(new ClassReader(bytes)), sw);
        return sw.toString();
    }

    public static String textify(byte[] bytes) {
        StringWriter sw = new StringWriter();
        new ClassReader(bytes).accept(new TraceClassVisitor(null, new Textifier(), new PrintWriter(sw)), ClassReader.SKIP_FRAMES);
        return sw.toString();
    }
}