package com.github.mouse0w0.instantcoffee;

import com.github.mouse0w0.instantcoffee.model.ClassDeclaration;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.objectweb.asm.ClassReader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class Cli {
    private static Path source;
    private static Path destination;
    private static boolean decompile;
    // Decompile options
    private static String indent;
    private static boolean skipLineNumber;
    private static boolean skipLocalVariable;
    private static boolean failOnUnsupported;

    public static void main(String[] args) {
        try {
            OptionParser parser = new OptionParser();
            OptionSpec<String> sourceSpec = parser.accepts("s", "Source file or directory")
                    .withRequiredArg()
                    .required();
            OptionSpec<String> destinationSpec = parser.accepts("d", "Destination file or directory")
                    .withRequiredArg()
                    .required();
            OptionSpec<Void> decompileSpec = parser.acceptsAll(Arrays.asList("D", "decompile"), "Decompile mode");

            OptionSpec<Integer> indentSpec = parser.accepts("indent", "Indent")
                    .availableIf(decompileSpec)
                    .withRequiredArg()
                    .ofType(Integer.class)
                    .defaultsTo(4);
            OptionSpec<Void> skipLineNumberSpec = parser.accepts("skip-line-number", "Skip line number")
                    .availableIf(decompileSpec);
            OptionSpec<Void> skipLocalVariableSpec = parser.accepts("skip-local-variable", "Skip local variable")
                    .availableIf(decompileSpec);
            OptionSpec<Void> failOnUnsupportedSpec = parser.accepts("fail-on-unsupported", "Fail on unsupported feature")
                    .availableIf(decompileSpec);

            OptionSet options;
            try {
                options = parser.parse(args);
            } catch (OptionException e) {
                System.out.println(e.getMessage());
                parser.printHelpOn(System.out);
                return;
            }

            source = Paths.get(options.valueOf(sourceSpec)).toAbsolutePath();
            destination = Paths.get(options.valueOf(destinationSpec)).toAbsolutePath();
            decompile = options.has(decompileSpec);
            indent = indent(options.valueOf(indentSpec));
            skipLineNumber = options.has(skipLineNumberSpec);
            skipLocalVariable = options.has(skipLocalVariableSpec);
            failOnUnsupported = options.has(failOnUnsupportedSpec);

            if (Files.notExists(source)) {
                System.err.println("Source file/directory does not exist: " + source);
                return;
            }

            compileOrDecompile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String indent(int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(" ");
        }
        return sb.toString();
    }

    private static void compileOrDecompile() throws IOException {
        AtomicInteger count = new AtomicInteger();
        if (Files.isDirectory(source)) {
            Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    String fileName = file.getFileName().toString();
                    if (decompile) {
                        if (fileName.endsWith(".class")) {
                            processFile(file);
                            count.incrementAndGet();
                        }
                    } else {
                        if (fileName.endsWith(".ic")) {
                            processFile(file);
                            count.incrementAndGet();
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } else {
            processFile(destination);
            count.incrementAndGet();
        }

        System.out.printf("%s %d file(s)%n", decompile ? "Decompiled" : "Compiled", count.get());
    }

    private static void processFile(Path file) throws IOException {
        if (decompile) {
            Decompiler decompiler = new Decompiler();
            decompiler.setFailOnUnsupportedFeature(failOnUnsupported);
            ClassReader classReader = new ClassReader(Files.readAllBytes(file));
            ClassDeclaration classDeclaration = decompiler.decompileClass(classReader);
            Unparser unparser = new Unparser();
            unparser.setIndent(indent);
            unparser.setSkipLineNumber(skipLineNumber);
            unparser.setSkipLocalVariable(skipLocalVariable);
            Path output = destination.resolve(String.join("/", classDeclaration.identifiers) + ".ic");
            try (BufferedWriter writer = Files.newBufferedWriter(ensureParentExists(output))) {
                unparser.unparseClass(classDeclaration, writer);
            }
        } else {
            try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                Parser parser = new Parser(reader);
                Compiler compiler = new Compiler();
                ClassFile classFile = compiler.compile(parser.parseClassDeclaration());
                Path output = destination.resolve(classFile.getClassInternalName() + ".class");
                Files.write(ensureParentExists(output), classFile.toByteArray());
            }
        }
    }

    private static Path ensureParentExists(Path path) throws IOException {
        Path parent = path.getParent();
        if (Files.notExists(parent)) {
            Files.createDirectories(parent);
        }
        return path;
    }
}
