package com.github.mouse0w0.instantcoffee;

import org.junit.jupiter.api.Test;

public class BlockTest {
    @Test
    public void test() {
        byte[] bytes = Utils.compile(
                "public class Block {\n" +
                        "  version 8\n" +
                        "\n" +
                        "  public static void method(int, int) {\n" +
                        "    A:\n" +
                        "      iload 0\n" +
                        "      ifne C\n" +
                        "    B:\n" +
                        "      iinc 0 1\n" +
                        "      goto D\n" +
                        "    C:\n" +
                        "      iinc 0 2\n" +
                        "    D:\n" +
                        "    {\n" +
                        "      A:\n" +
                        "        iload 1\n" +
                        "        ifeq C\n" +
                        "      B:\n" +
                        "        iinc 1 1\n" +
                        "        goto D\n" +
                        "      C:\n" +
                        "        iinc 1 2\n" +
                        "      D:\n" +
                        "    }\n" +
                        "    return\n" +
                        "  }\n" +
                        "}\n");
        System.out.println(Utils.decompile(bytes));
    }
}
