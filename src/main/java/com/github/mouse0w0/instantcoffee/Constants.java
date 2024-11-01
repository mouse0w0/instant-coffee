package com.github.mouse0w0.instantcoffee;

import org.objectweb.asm.Opcodes;

import java.util.HashMap;
import java.util.Map;

public class Constants implements Opcodes {
    public static final String[] EMPTY_STRING_ARRAY = {};

    public static final int NOT_FOUND = -1;

    public static final int FLAG_INTERFACE = 0x100;

    //    public static final int LDC_W = 19;
    //    public static final int LDC2_W = 20;
    //    public static final int ILOAD_0 = 26;
    //    public static final int ILOAD_1 = 27;
    //    public static final int ILOAD_2 = 28;
    //    public static final int ILOAD_3 = 29;
    //    public static final int LLOAD_0 = 30;
    //    public static final int LLOAD_1 = 31;
    //    public static final int LLOAD_2 = 32;
    //    public static final int LLOAD_3 = 33;
    //    public static final int FLOAD_0 = 34;
    //    public static final int FLOAD_1 = 35;
    //    public static final int FLOAD_2 = 36;
    //    public static final int FLOAD_3 = 37;
    //    public static final int DLOAD_0 = 38;
    //    public static final int DLOAD_1 = 39;
    //    public static final int DLOAD_2 = 40;
    //    public static final int DLOAD_3 = 41;
    //    public static final int ALOAD_0 = 42;
    //    public static final int ALOAD_1 = 43;
    //    public static final int ALOAD_2 = 44;
    //    public static final int ALOAD_3 = 45;
    //    public static final int ISTORE_0 = 59;
    //    public static final int ISTORE_1 = 60;
    //    public static final int ISTORE_2 = 61;
    //    public static final int ISTORE_3 = 62;
    //    public static final int LSTORE_0 = 63;
    //    public static final int LSTORE_1 = 64;
    //    public static final int LSTORE_2 = 65;
    //    public static final int LSTORE_3 = 66;
    //    public static final int FSTORE_0 = 67;
    //    public static final int FSTORE_1 = 68;
    //    public static final int FSTORE_2 = 69;
    //    public static final int FSTORE_3 = 70;
    //    public static final int DSTORE_0 = 71;
    //    public static final int DSTORE_1 = 72;
    //    public static final int DSTORE_2 = 73;
    //    public static final int DSTORE_3 = 74;
    //    public static final int ASTORE_0 = 75;
    //    public static final int ASTORE_1 = 76;
    //    public static final int ASTORE_2 = 77;
    //    public static final int ASTORE_3 = 78;
    //    public static final int WIDE = 196;
    //    public static final int GOTO_W = 200;
    //    public static final int JSR_W = 201;

    public static final int SWITCH = 202;
    public static final int LINE_NUMBER = 203;
    public static final int LOCAL_VARIABLE = 204;
    public static final int TRY_CATCH_BLOCK = 205;

    private static final Map<Integer, String> opcodeToName = new HashMap<>();
    private static final Map<String, Integer> nameToOpcode = new HashMap<>();

    public static String getOpcodeName(int opcode) {
        return opcodeToName.get(opcode);
    }

    public static int getOpcode(String name) {
        Integer opcode = nameToOpcode.get(name);
        return opcode != null ? opcode : NOT_FOUND;
    }

    private static void putOpcode(Integer opcode, String name) {
        opcodeToName.put(opcode, name);
        nameToOpcode.put(name, opcode);
    }

    static {
        putOpcode(NOP, "nop");
        putOpcode(ACONST_NULL, "aconst_null");
        putOpcode(ICONST_M1, "iconst_m1");
        putOpcode(ICONST_0, "iconst_0");
        putOpcode(ICONST_1, "iconst_1");
        putOpcode(ICONST_2, "iconst_2");
        putOpcode(ICONST_3, "iconst_3");
        putOpcode(ICONST_4, "iconst_4");
        putOpcode(ICONST_5, "iconst_5");
        putOpcode(LCONST_0, "lconst_0");
        putOpcode(LCONST_1, "lconst_1");
        putOpcode(FCONST_0, "fconst_0");
        putOpcode(FCONST_1, "fconst_1");
        putOpcode(FCONST_2, "fconst_2");
        putOpcode(DCONST_0, "dconst_0");
        putOpcode(DCONST_1, "dconst_1");
        putOpcode(BIPUSH, "bipush");
        putOpcode(SIPUSH, "sipush");
        putOpcode(LDC, "ldc");
        //        putOpcode(LDC_W, "ldc_w");
        //        putOpcode(LDC2_W, "ldc2_w");
        putOpcode(ILOAD, "iload");
        putOpcode(LLOAD, "lload");
        putOpcode(FLOAD, "fload");
        putOpcode(DLOAD, "dload");
        putOpcode(ALOAD, "aload");
        //        putOpcode(ILOAD_0, "iload_0");
        //        putOpcode(ILOAD_1, "iload_1");
        //        putOpcode(ILOAD_2, "iload_2");
        //        putOpcode(ILOAD_3, "iload_3");
        //        putOpcode(LLOAD_0, "lload_0");
        //        putOpcode(LLOAD_1, "lload_1");
        //        putOpcode(LLOAD_2, "lload_2");
        //        putOpcode(LLOAD_3, "lload_3");
        //        putOpcode(FLOAD_0, "fload_0");
        //        putOpcode(FLOAD_1, "fload_1");
        //        putOpcode(FLOAD_2, "fload_2");
        //        putOpcode(FLOAD_3, "fload_3");
        //        putOpcode(DLOAD_0, "dload_0");
        //        putOpcode(DLOAD_1, "dload_1");
        //        putOpcode(DLOAD_2, "dload_2");
        //        putOpcode(DLOAD_3, "dload_3");
        //        putOpcode(ALOAD_0, "aload_0");
        //        putOpcode(ALOAD_1, "aload_1");
        //        putOpcode(ALOAD_2, "aload_2");
        //        putOpcode(ALOAD_3, "aload_3");
        putOpcode(IALOAD, "iaload");
        putOpcode(LALOAD, "laload");
        putOpcode(FALOAD, "faload");
        putOpcode(DALOAD, "daload");
        putOpcode(AALOAD, "aaload");
        putOpcode(BALOAD, "baload");
        putOpcode(CALOAD, "caload");
        putOpcode(SALOAD, "saload");
        putOpcode(ISTORE, "istore");
        putOpcode(LSTORE, "lstore");
        putOpcode(FSTORE, "fstore");
        putOpcode(DSTORE, "dstore");
        putOpcode(ASTORE, "astore");
        //        putOpcode(ISTORE_0, "istore_0");
        //        putOpcode(ISTORE_1, "istore_1");
        //        putOpcode(ISTORE_2, "istore_2");
        //        putOpcode(ISTORE_3, "istore_3");
        //        putOpcode(LSTORE_0, "lstore_0");
        //        putOpcode(LSTORE_1, "lstore_1");
        //        putOpcode(LSTORE_2, "lstore_2");
        //        putOpcode(LSTORE_3, "lstore_3");
        //        putOpcode(FSTORE_0, "fstore_0");
        //        putOpcode(FSTORE_1, "fstore_1");
        //        putOpcode(FSTORE_2, "fstore_2");
        //        putOpcode(FSTORE_3, "fstore_3");
        //        putOpcode(DSTORE_0, "dstore_0");
        //        putOpcode(DSTORE_1, "dstore_1");
        //        putOpcode(DSTORE_2, "dstore_2");
        //        putOpcode(DSTORE_3, "dstore_3");
        //        putOpcode(ASTORE_0, "astore_0");
        //        putOpcode(ASTORE_1, "astore_1");
        //        putOpcode(ASTORE_2, "astore_2");
        //        putOpcode(ASTORE_3, "astore_3");
        putOpcode(IASTORE, "iastore");
        putOpcode(LASTORE, "lastore");
        putOpcode(FASTORE, "fastore");
        putOpcode(DASTORE, "dastore");
        putOpcode(AASTORE, "aastore");
        putOpcode(BASTORE, "bastore");
        putOpcode(CASTORE, "castore");
        putOpcode(SASTORE, "sastore");
        putOpcode(POP, "pop");
        putOpcode(POP2, "pop2");
        putOpcode(DUP, "dup");
        putOpcode(DUP_X1, "dup_x1");
        putOpcode(DUP_X2, "dup_x2");
        putOpcode(DUP2, "dup2");
        putOpcode(DUP2_X1, "dup2_x1");
        putOpcode(DUP2_X2, "dup2_x2");
        putOpcode(SWAP, "swap");
        putOpcode(IADD, "iadd");
        putOpcode(LADD, "ladd");
        putOpcode(FADD, "fadd");
        putOpcode(DADD, "dadd");
        putOpcode(ISUB, "isub");
        putOpcode(LSUB, "lsub");
        putOpcode(FSUB, "fsub");
        putOpcode(DSUB, "dsub");
        putOpcode(IMUL, "imul");
        putOpcode(LMUL, "lmul");
        putOpcode(FMUL, "fmul");
        putOpcode(DMUL, "dmul");
        putOpcode(IDIV, "idiv");
        putOpcode(LDIV, "ldiv");
        putOpcode(FDIV, "fdiv");
        putOpcode(DDIV, "ddiv");
        putOpcode(IREM, "irem");
        putOpcode(LREM, "lrem");
        putOpcode(FREM, "frem");
        putOpcode(DREM, "drem");
        putOpcode(INEG, "ineg");
        putOpcode(LNEG, "lneg");
        putOpcode(FNEG, "fneg");
        putOpcode(DNEG, "dneg");
        putOpcode(ISHL, "ishl");
        putOpcode(LSHL, "lshl");
        putOpcode(ISHR, "ishr");
        putOpcode(LSHR, "lshr");
        putOpcode(IUSHR, "iushr");
        putOpcode(LUSHR, "lushr");
        putOpcode(IAND, "iand");
        putOpcode(LAND, "land");
        putOpcode(IOR, "ior");
        putOpcode(LOR, "lor");
        putOpcode(IXOR, "ixor");
        putOpcode(LXOR, "lxor");
        putOpcode(IINC, "iinc");
        putOpcode(I2L, "i2l");
        putOpcode(I2F, "i2f");
        putOpcode(I2D, "i2d");
        putOpcode(L2I, "l2i");
        putOpcode(L2F, "l2f");
        putOpcode(L2D, "l2d");
        putOpcode(F2I, "f2i");
        putOpcode(F2L, "f2l");
        putOpcode(F2D, "f2d");
        putOpcode(D2I, "d2i");
        putOpcode(D2L, "d2l");
        putOpcode(D2F, "d2f");
        putOpcode(I2B, "i2b");
        putOpcode(I2C, "i2c");
        putOpcode(I2S, "i2s");
        putOpcode(LCMP, "lcmp");
        putOpcode(FCMPL, "fcmpl");
        putOpcode(FCMPG, "fcmpg");
        putOpcode(DCMPL, "dcmpl");
        putOpcode(DCMPG, "dcmpg");
        putOpcode(IFEQ, "ifeq");
        putOpcode(IFNE, "ifne");
        putOpcode(IFLT, "iflt");
        putOpcode(IFGE, "ifge");
        putOpcode(IFGT, "ifgt");
        putOpcode(IFLE, "ifle");
        putOpcode(IF_ICMPEQ, "if_icmpeq");
        putOpcode(IF_ICMPNE, "if_icmpne");
        putOpcode(IF_ICMPLT, "if_icmplt");
        putOpcode(IF_ICMPGE, "if_icmpge");
        putOpcode(IF_ICMPGT, "if_icmpgt");
        putOpcode(IF_ICMPLE, "if_icmple");
        putOpcode(IF_ACMPEQ, "if_acmpeq");
        putOpcode(IF_ACMPNE, "if_acmpne");
        putOpcode(GOTO, "goto");
        putOpcode(JSR, "jsr");
        putOpcode(RET, "ret");
        putOpcode(TABLESWITCH, "tableswitch");
        putOpcode(LOOKUPSWITCH, "lookupswitch");
        putOpcode(IRETURN, "ireturn");
        putOpcode(LRETURN, "lreturn");
        putOpcode(FRETURN, "freturn");
        putOpcode(DRETURN, "dreturn");
        putOpcode(ARETURN, "areturn");
        putOpcode(RETURN, "return");
        putOpcode(GETSTATIC, "getstatic");
        putOpcode(PUTSTATIC, "putstatic");
        putOpcode(GETFIELD, "getfield");
        putOpcode(PUTFIELD, "putfield");
        putOpcode(INVOKEVIRTUAL, "invokevirtual");
        putOpcode(INVOKESPECIAL, "invokespecial");
        putOpcode(INVOKESTATIC, "invokestatic");
        putOpcode(INVOKEVIRTUAL | FLAG_INTERFACE, "invokevirtualinterface");
        putOpcode(INVOKESPECIAL | FLAG_INTERFACE, "invokespecialinterface");
        putOpcode(INVOKESTATIC | FLAG_INTERFACE, "invokestaticinterface");
        putOpcode(INVOKEINTERFACE | FLAG_INTERFACE, "invokeinterface");
        putOpcode(INVOKEDYNAMIC, "invokedynamic");
        putOpcode(NEW, "new");
        putOpcode(NEWARRAY, "newarray");
        putOpcode(ANEWARRAY, "anewarray");
        putOpcode(ARRAYLENGTH, "arraylength");
        putOpcode(ATHROW, "athrow");
        putOpcode(CHECKCAST, "checkcast");
        putOpcode(INSTANCEOF, "instanceof");
        putOpcode(MONITORENTER, "monitorenter");
        putOpcode(MONITOREXIT, "monitorexit");
        //        putOpcode(WIDE, "wide");
        putOpcode(MULTIANEWARRAY, "multianewarray");
        putOpcode(IFNULL, "ifnull");
        putOpcode(IFNONNULL, "ifnonnull");
        //        putOpcode(GOTO_W, "goto_w");
        //        putOpcode(JSR_W, "jsr_w");

        putOpcode(SWITCH, "switch");
        putOpcode(LINE_NUMBER, "line");
        putOpcode(LOCAL_VARIABLE, "var");
        putOpcode(TRY_CATCH_BLOCK, "try");

    }

    private static final Map<Integer, String> handleKindName = new HashMap<>();
    private static final Map<String, Integer> handleKind = new HashMap<>();

    public static String getHandleKindName(int kind) {
        return handleKindName.get(kind);
    }

    public static int getHandleKind(String name) {
        return handleKind.get(name);
    }

    private static void putHandleKind(Integer kind, String name) {
        handleKindName.put(kind, name);
        handleKind.put(name, kind);
    }

    static {
        putHandleKind(H_GETFIELD, "getfield");
        putHandleKind(H_GETSTATIC, "getstatic");
        putHandleKind(H_PUTFIELD, "putfield");
        putHandleKind(H_PUTSTATIC, "putstatic");
        putHandleKind(H_INVOKEVIRTUAL, "invokevirtual");
        putHandleKind(H_INVOKESTATIC, "invokestatic");
        putHandleKind(H_INVOKESPECIAL, "invokespecial");
        putHandleKind(H_NEWINVOKESPECIAL, "newinvokespecial");
        putHandleKind(H_INVOKEINTERFACE | FLAG_INTERFACE, "invokeinterface");
        putHandleKind(H_INVOKESTATIC | FLAG_INTERFACE, "invokestaticinterface");
        putHandleKind(H_INVOKESPECIAL | FLAG_INTERFACE, "invokespecialinterface");
        putHandleKind(H_INVOKEVIRTUAL | FLAG_INTERFACE, "invokevirtualinterface");
        putHandleKind(H_NEWINVOKESPECIAL | FLAG_INTERFACE, "newinvokespecialinterface");
    }
}
