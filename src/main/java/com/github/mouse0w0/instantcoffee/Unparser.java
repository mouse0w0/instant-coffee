package com.github.mouse0w0.instantcoffee;

import com.github.mouse0w0.instantcoffee.model.*;
import com.github.mouse0w0.instantcoffee.model.insn.*;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;

public class Unparser {
    private static final String NO_INDENT = "";
    private static final String INDENT = "    ";
    private static final String INDENT2 = INDENT + "  ";
    private static final String INDENT3 = INDENT + INDENT;

    private boolean skipLineNumber;
    private boolean skipLocalVariable;

    public static void unparse(ClassDeclaration cd, Writer writer) {
        new Unparser().unparseClass(cd, writer);
    }

    public static void unparse(ClassDeclaration cd, PrintWriter pw) {
        new Unparser().unparseClass(cd, pw);
    }

    public boolean isSkipLineNumber() {
        return skipLineNumber;
    }

    public void setSkipLineNumber(boolean skipLineNumber) {
        this.skipLineNumber = skipLineNumber;
    }

    public boolean isSkipLocalVariable() {
        return skipLocalVariable;
    }

    public void setSkipLocalVariable(boolean skipLocalVariable) {
        this.skipLocalVariable = skipLocalVariable;
    }

    public void unparseClass(ClassDeclaration cd, Writer writer) {
        unparseClass(cd, new PrintWriter(writer));
    }

    public void unparseClass(ClassDeclaration cd, PrintWriter pw) {
        unparseAnnotations(cd.annotations, NO_INDENT, pw);
        unparseModifiers(cd.modifiers, pw);
        unparseIdentifiers(cd.identifiers, pw);
        unparseTypeParameters(cd.typeParameters, pw);
        pw.append(" ");
        unparseSuperclass(cd.superclass, pw);
        unparseInterfaces(cd.interfaces, pw);
        pw.println("{");
        unparseSource(cd.source, pw);
        for (InnerClassDeclaration innerClass : cd.innerClasses) {
            unparseInnerClass(innerClass, pw);
        }
        for (FieldDeclaration field : cd.fields) {
            unparseField(field, pw);
        }
        for (MethodDeclaration method : cd.methods) {
            unparseMethod(method, pw);
        }
        pw.println("}");
    }

    private void unparseModifiers(Modifier[] modifiers, PrintWriter pw) {
        for (Modifier modifier : modifiers) {
            pw.append(modifier.toString()).append(" ");
        }
    }

    private void unparseIdentifiers(String[] identifiers, PrintWriter pw) {
        pw.append(String.join(".", identifiers));
    }

    private void unparseTypeParameters(TypeParameter[] typeParameters, PrintWriter pw) {
        if (typeParameters.length == 0) {
            return;
        }
        pw.append("<");
        unparseTypeParameter(typeParameters[0], pw);
        for (int i = 1; i < typeParameters.length; i++) {
            pw.append(", ");
            unparseTypeParameter(typeParameters[i], pw);
        }
        pw.append(">");
    }

    private void unparseTypeParameter(TypeParameter typeParameter, PrintWriter pw) {
        pw.append(typeParameter.name);
        ReferenceType[] bounds = typeParameter.bounds;
        if (bounds.length != 0) {
            if (bounds[0] != null) {
                pw.append(" extends ");
            } else {
                pw.append(" implements ");
            }
            boolean firstVisited = false;
            for (ReferenceType bound : bounds) {
                if (bound == null) continue;
                if (firstVisited) pw.append(" & ");
                else firstVisited = true;
                pw.append(bound.toString());
            }
        }
    }

    private void unparseSuperclass(ReferenceType superclass, PrintWriter pw) {
        if (superclass == null) return;
        pw.append("extends ").append(superclass.toString()).append(" ");
    }

    private void unparseInterfaces(ReferenceType[] interfaces, PrintWriter pw) {
        if (interfaces.length == 0) return;
        boolean firstVisited = false;
        pw.append("implements ");
        for (ReferenceType inte : interfaces) {
            if (firstVisited) pw.append(", ");
            else firstVisited = true;
            pw.append(inte.toString());
        }
        pw.append(" ");
    }

    private void unparseAnnotations(Annotation[] annotations, String indent, PrintWriter pw) {
        for (Annotation annotation : annotations) {
            pw.append(indent);
            unparseAnnotation(annotation, pw);
            pw.println();
        }
    }

    private void unparseAnnotation(Annotation annotation, PrintWriter pw) {
        pw.append("@").append(annotation.type.toString()).append("(");
        boolean firstVisited = false;
        for (AnnotationValuePair pair : annotation.pairs) {
            if (firstVisited) pw.append(", ");
            else firstVisited = true;
            pw.append(pair.key).append(" = ");
            unparseAnnotationValue(pair.value, pw);
        }
        pw.append(")");
        if (!annotation.visible) pw.append(" invisible");
    }

    private void unparseAnnotationValue(AnnotationValue value, PrintWriter pw) {
        if (value instanceof Annotation) {
            unparseAnnotation((Annotation) value, pw);
        } else if (value instanceof AnnotationValueArrayInitializer) {
            unparseAnnotationValueArray((AnnotationValueArrayInitializer) value, pw);
        } else {
            pw.append(value.toString());
        }
    }

    private void unparseAnnotationValueArray(AnnotationValueArrayInitializer array, PrintWriter pw) {
        boolean firstVisited = false;
        pw.append("{");
        for (AnnotationValue value : array.values) {
            if (firstVisited) pw.append(", ");
            else firstVisited = true;
            unparseAnnotationValue(value, pw);
        }
        pw.append("}");
    }

    private void unparseSource(SourceDeclaration source, PrintWriter pw) {
        if (source == null) return;
        pw.println();
        pw.append(INDENT).append("source ").append(source.file.toString());
        pw.println();
    }

    private void unparseInnerClass(InnerClassDeclaration innerClass, PrintWriter pw) {
        pw.println();
        pw.append(INDENT);
        unparseModifiers(innerClass.modifiers, pw);
        pw.append("innerclass ");
        unparseIdentifiers(innerClass.identifiers, pw);
        pw.println();
    }

    private void unparseField(FieldDeclaration field, PrintWriter pw) {
        pw.println();
        unparseAnnotations(field.annotations, INDENT, pw);
        pw.append(INDENT);
        unparseModifiers(field.modifiers, pw);
        pw.append(field.type.toString()).append(" ").append(field.name);
        if (!(field.value instanceof NullLiteral)) {
            pw.append(" = ").append(field.value.toString());
        }
        pw.println();
    }

    private void unparseMethod(MethodDeclaration method, PrintWriter pw) {
        pw.println();
        unparseAnnotations(method.annotations, INDENT, pw);
        if ("<clinit>".equals(method.name)) {
            pw.append(INDENT).append("static");
        } else {
            pw.append(INDENT);
            unparseModifiers(method.modifiers, pw);
            unparseTypeParameters(method.typeParameters, pw);
            if ("<init>".equals(method.name)) {
                pw.append("<init>");
            } else {
                pw.append(method.returnType.toString()).append(" ").append(method.name);
            }
            pw.append("(");
            unparseMethodParameters(method.parameterTypes, pw);
            pw.append(")");
            unparseMethodExceptions(method.exceptionTypes, pw);
        }
        pw.println(" {");
        unparseInstructions(method.instructions, pw);
        unparseLocalVariables(method.localVariables, pw);
        unparseTryCatchBlock(method.tryCatchBlocks, pw);
        pw.append(INDENT).println("}");
    }

    private void unparseMethodParameters(Type[] parameterTypes, PrintWriter pw) {
        if (parameterTypes.length == 0) return;
        boolean firstVisited = false;
        for (Type parameterType : parameterTypes) {
            if (firstVisited) pw.append(", ");
            else firstVisited = true;
            pw.append(parameterType.toString());
        }
    }

    private void unparseMethodExceptions(Type[] exceptionTypes, PrintWriter pw) {
        if (exceptionTypes.length == 0) return;
        pw.append(" throws");
        boolean firstVisited = false;
        for (Type exceptionType : exceptionTypes) {
            if (firstVisited) pw.append(", ");
            else firstVisited = true;
            pw.append(exceptionType.toString());
        }
    }

    private void unparseInstructions(List<BaseInsn> instructions, PrintWriter pw) {
        for (BaseInsn insn : instructions) {
            if (insn instanceof Insn) {
                unparseInsn((Insn) insn, pw);
            } else if (insn instanceof IntInsn) {
                unparseIntInsn((IntInsn) insn, pw);
            } else if (insn instanceof VarInsn) {
                unparseVarInsn((VarInsn) insn, pw);
            } else if (insn instanceof TypeInsn) {
                unparseTypeInsn((TypeInsn) insn, pw);
            } else if (insn instanceof FieldInsn) {
                unparseFieldInsn((FieldInsn) insn, pw);
            } else if (insn instanceof MethodInsn) {
                unparseMethodInsn((MethodInsn) insn, pw);
            } else if (insn instanceof JumpInsn) {
                unparseJumpInsn((JumpInsn) insn, pw);
            } else if (insn instanceof LabelInsn) {
                unparseLabelInsn((LabelInsn) insn, pw);
            } else if (insn instanceof LdcInsn) {
                unparseLdcInsn((LdcInsn) insn, pw);
            } else if (insn instanceof IincInsn) {
                unparseIincInsn((IincInsn) insn, pw);
            } else if (insn instanceof SwitchInsn) {
                unparseSwitchInsn((SwitchInsn) insn, pw);
            } else if (insn instanceof MultiANewArrayInsn) {
                unparseMultiANewArrayInsn((MultiANewArrayInsn) insn, pw);
            } else if (insn instanceof LineNumberInsn) {
                unparseLineNumberInsn((LineNumberInsn) insn, pw);
            } else {
                throw new IllegalArgumentException();
            }
        }
    }

    private void unparseInsn(Insn insn, PrintWriter pw) {
        pw.append(INDENT3).append(insn.opcode).println();
    }

    private void unparseIntInsn(IntInsn intInsn, PrintWriter pw) {
        pw.append(INDENT3).append(intInsn.opcode).append(" ").append(intInsn.operand.toString()).println();
    }

    private void unparseVarInsn(VarInsn varInsn, PrintWriter pw) {
        pw.append(INDENT3).append(varInsn.opcode).append(" ").append(varInsn.var.toString()).println();
    }

    private void unparseTypeInsn(TypeInsn typeInsn, PrintWriter pw) {
        pw.append(INDENT3).append(typeInsn.opcode).append(" ").append(typeInsn.type.toString()).println();
    }

    private void unparseFieldInsn(FieldInsn fieldInsn, PrintWriter pw) {
        pw.append(INDENT3).append(fieldInsn.opcode);
        pw.append(" ").append(fieldInsn.owner.toString());
        pw.append(" ").append(fieldInsn.name);
        pw.append(" ").append(fieldInsn.type.toString());
        pw.println();
    }

    private void unparseMethodInsn(MethodInsn methodInsn, PrintWriter pw) {
        pw.append(INDENT3).append(methodInsn.opcode);
        pw.append(" ").append(methodInsn.owner.toString());
        pw.append(" ").append(methodInsn.name).append(" (");
        unparseMethodParameters(methodInsn.parameterTypes, pw);
        pw.append(")").append(methodInsn.returnType.toString());
        pw.println();
    }

    private void unparseJumpInsn(JumpInsn jumpInsn, PrintWriter pw) {
        pw.append(INDENT3).append(jumpInsn.opcode).append(" ").append(jumpInsn.label).println();
    }

    private void unparseLabelInsn(LabelInsn labelInsn, PrintWriter pw) {
        pw.append(INDENT2).append(labelInsn.name).append(":").println();
    }

    private void unparseLdcInsn(LdcInsn ldcInsn, PrintWriter pw) {
        pw.append(INDENT3).append(ldcInsn.opcode).append(" ").append(ldcInsn.value.toString()).println();
    }

    private void unparseIincInsn(IincInsn iincInsn, PrintWriter pw) {
        pw.append(INDENT3).append(iincInsn.opcode);
        pw.append(" ").append(iincInsn.var.toString());
        pw.append(" ").append(iincInsn.increment.toString());
        pw.println();
    }

    private void unparseSwitchInsn(SwitchInsn switchInsn, PrintWriter pw) {
        throw new UnsupportedOperationException("switch");
    }

    private void unparseMultiANewArrayInsn(MultiANewArrayInsn multiANewArrayInsn, PrintWriter pw) {
        pw.append(INDENT3).append(multiANewArrayInsn.opcode);
        pw.append(" ").append(multiANewArrayInsn.type.toString());
        pw.append(" ").append(multiANewArrayInsn.numDimensions.toString());

        pw.println();
    }

    private void unparseLineNumberInsn(LineNumberInsn lineNumberInsn, PrintWriter pw) {
        if (skipLineNumber) return;
        pw.append(INDENT3).append("line");
        pw.append(" ").append(lineNumberInsn.line.toString());
        pw.append(" ").append(lineNumberInsn.label);
        pw.println();
    }

    private void unparseLocalVariables(List<LocalVariable> localVariables, PrintWriter pw) {
        if (skipLocalVariable) return;
        for (LocalVariable localVariable : localVariables) {
            pw.append(INDENT3).append("local")
                    .append(" ").append(localVariable.name)
                    .append(" ").append(localVariable.type.toString())
                    .append(" ").append(localVariable.start)
                    .append(" ").append(localVariable.end)
                    .append(" ").append(localVariable.index.toString())
                    .println();
        }
    }

    private void unparseTryCatchBlock(List<TryCatchBlock> tryCatchBlocks, PrintWriter pw) {
        for (TryCatchBlock tryCatchBlock : tryCatchBlocks) {
            pw.append(INDENT3).append("catch")
                    .append(tryCatchBlock.start)
                    .append(tryCatchBlock.end)
                    .append(tryCatchBlock.handler)
                    .append(tryCatchBlock.type != null ? tryCatchBlock.type.toString() : "finally");
        }
    }
}
