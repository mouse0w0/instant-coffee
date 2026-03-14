package com.github.mouse0w0.instantcoffee;

import com.github.mouse0w0.instantcoffee.model.ClassDeclaration;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public final class Cli {
    private static Path source;
    private static Path destination;
    private static boolean decompile;
    // Decompile options
    private static String indent;
    private static boolean skipLineNumber;
    private static boolean skipLocalVariable;
    private static boolean failOnUnsupported;
    private static boolean printTextify;
    private static boolean validate;

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
            OptionSpec<Void> printTextifySpec = parser.acceptsAll(Arrays.asList("T", "print-textify"), "Print textify")
                    .availableIf(decompileSpec);
            OptionSpec<Void> validateSpec = parser.acceptsAll(Arrays.asList("V", "validate"), "Validate mode");

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
            validate = options.has(validateSpec);
            indent = indent(options.valueOf(indentSpec));
            skipLineNumber = options.has(skipLineNumberSpec);
            skipLocalVariable = options.has(skipLocalVariableSpec);
            failOnUnsupported = options.has(failOnUnsupportedSpec);
            printTextify = options.has(printTextifySpec);

            if (Files.notExists(source)) {
                System.err.println("Source file/directory does not exist: " + source);
                return;
            }

            process();
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

    private static void process() throws IOException {
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

            Unparser unparser = new Unparser();
            unparser.setIndent(indent);
            unparser.setSkipLineNumber(skipLineNumber);
            unparser.setSkipLocalVariable(skipLocalVariable);

            ClassReader classReader = new ClassReader(Files.readAllBytes(file));
            ClassDeclaration classDeclaration = decompiler.decompileClass(classReader);
            String classInternalName = String.join("/", classDeclaration.identifiers);

            StringWriter icWriter = new StringWriter();
            unparser.unparseClass(classDeclaration, icWriter);
            String ic = icWriter.toString();
            writeString(destination.resolve(classInternalName + ".ic"), ic, StandardCharsets.UTF_8);

            String textify = null;
            if (validate || printTextify) {
                StringWriter textifyWriter = new StringWriter();
                classReader.accept(new TraceClassVisitor(null, new Textifier(), new PrintWriter(textifyWriter)), ClassReader.SKIP_FRAMES);
                textify = textifyWriter.toString();
                writeString(destination.resolve(classInternalName + ".txt"), textify, StandardCharsets.UTF_8);
            }

            if (validate) {
                StringReader icReader = new StringReader(ic);
                Parser parser = new Parser(icReader);
                Compiler compiler = new Compiler();
                ClassFile recompiledClassFile = compiler.compile(parser.parseClassDeclaration());
                byte[] recompiledBytes = recompiledClassFile.toByteArray();
                ClassReader recompiledClassReader = new ClassReader(recompiledBytes);

                StringWriter recompiledIcWriter = new StringWriter();
                unparser.unparseClass(decompiler.decompileClass(recompiledClassReader), recompiledIcWriter);
                String recompiledIc = recompiledIcWriter.toString();
                writeString(destination.resolve(classInternalName + ".recompiled.ic"), recompiledIc, StandardCharsets.UTF_8);

                StringWriter recompiledTextifyWriter = new StringWriter();
                recompiledClassReader.accept(new TraceClassVisitor(null, new Textifier(), new PrintWriter(recompiledTextifyWriter)), ClassReader.SKIP_FRAMES);
                String recompiledTextify = recompiledTextifyWriter.toString();
                writeString(destination.resolve(classInternalName + ".recompiled.txt"), recompiledTextify, StandardCharsets.UTF_8);

                boolean icMatch = ic.equals(recompiledIc);
                boolean textifyMatch = textify.equals(recompiledTextify);

                if (!icMatch || !textifyMatch) {
                    System.out.printf("%s: Decompilation: %s Textify: %s%n", source.relativize(file), icMatch ? "PASS" : "FAIL", textifyMatch ? "PASS" : "FAIL");
                }
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

    private static void writeString(Path path, String string, Charset charset, OpenOption... options) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(ensureParentExists(path), charset, options)) {
            writer.write(string);
        }
    }

    private Cli() {
        throw new Error();
    }
}
