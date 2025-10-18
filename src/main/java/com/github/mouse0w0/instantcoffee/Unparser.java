package com.github.mouse0w0.instantcoffee;

import com.github.mouse0w0.instantcoffee.model.*;
import com.github.mouse0w0.instantcoffee.model.statement.*;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

public class Unparser {
    private String indent = "  ";
    private boolean skipLineNumber;
    private boolean skipLocalVariable;

    public static void unparse(ClassDeclaration cd, Writer writer) {
        new Unparser().unparseClass(cd, writer);
    }

    public static void unparse(ClassDeclaration cd, PrintWriter pw) {
        new Unparser().unparseClass(cd, pw);
    }

    public String getIndent() {
        return indent;
    }

    public void setIndent(String indent) {
        this.indent = indent;
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

    private int depth;

    private void push() {
        depth++;
    }

    private void push2() {
        depth += 2;
    }

    private void pop() {
        if (depth <= 0) {
            throw new IllegalStateException();
        }
        depth--;
    }

    private void pop2() {
        if (depth <= 1) {
            throw new IllegalStateException();
        }
        depth -= 2;
    }

    private PrintWriter appendIndent(PrintWriter pw) {
        for (int i = 0; i < depth; i++) {
            pw.append(indent);
        }
        return pw;
    }

    public void unparseClass(ClassDeclaration cd, Writer writer) {
        unparseClass(cd, new PrintWriter(writer));
    }

    public void unparseClass(ClassDeclaration cd, PrintWriter pw) {
        unparseAnnotations(cd.annotations, pw);
        push();
        unparseModifiers(cd.modifiers, pw);
        unparseIdentifiers(cd.identifiers, pw);
        unparseSuperclass(cd.superclass, pw);
        unparseInterfaces(cd.interfaces, pw);
        pw.append(" {").println();
        unparseVersion(cd.version, pw);
        unparseSource(cd.source, pw);
        unparseNestHost(cd.nestHost, pw);
        for (ReferenceType nestMember : cd.nestMembers) {
            unparseNestMember(nestMember, pw);
        }
        for (InnerClassDeclaration innerClass : cd.innerClasses) {
            unparseInnerClass(innerClass, pw);
        }
        for (FieldDeclaration field : cd.fields) {
            unparseField(field, pw);
        }
        for (MethodDeclaration method : cd.methods) {
            unparseMethod(method, pw);
        }
        pw.append("}").println();
        pop();
    }

    private void unparseModifiers(List<Modifier> modifiers, PrintWriter pw) {
        for (Modifier modifier : modifiers) {
            pw.append(modifier.toString()).append(" ");
        }
    }

    private void unparseIdentifiers(String[] identifiers, PrintWriter pw) {
        if (identifiers.length != 0) {
            pw.append(identifiers[0]);
            for (int i = 1; i < identifiers.length; i++) {
                pw.append(".").append(identifiers[i]);
            }
        }
    }

    private void unparseSuperclass(ReferenceType superclass, PrintWriter pw) {
        if (superclass == null) return;
        pw.append(" extends ").append(superclass.toString());
    }

    private void unparseInterfaces(List<ReferenceType> interfaces, PrintWriter pw) {
        if (interfaces.isEmpty()) return;
        boolean firstVisited = false;
        pw.append(" implements ");
        for (ReferenceType inte : interfaces) {
            if (firstVisited) pw.append(", ");
            else firstVisited = true;
            pw.append(inte.toString());
        }
    }

    private void unparseAnnotations(List<Annotation> annotations, PrintWriter pw) {
        for (Annotation annotation : annotations) {
            appendIndent(pw);
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

    private void unparseVersion(IntegerLiteral version, PrintWriter pw) {
        pw.println();
        appendIndent(pw).append("version ").append(version.value);
        pw.println();
    }

    private void unparseSource(StringLiteral source, PrintWriter pw) {
        if (source == null) return;
        pw.println();
        appendIndent(pw).append("source ").append(source.toString());
        pw.println();
    }

    private void unparseNestHost(ReferenceType nestHost, PrintWriter pw) {
        if (nestHost == null) return;
        pw.println();
        appendIndent(pw).append("nesthost ").append(nestHost.toString());
        pw.println();
    }

    private void unparseNestMember(ReferenceType nestMember, PrintWriter pw) {
        pw.println();
        appendIndent(pw).append("nestmember ").append(nestMember.toString());
        pw.println();
    }

    private void unparseInnerClass(InnerClassDeclaration icd, PrintWriter pw) {
        pw.println();
        appendIndent(pw);
        unparseModifiers(icd.modifiers, pw);

        switch (icd.type) {
            case ANONYMOUS:
                pw.append("anonymous innerclass ");
                unparseIdentifiers(icd.name, pw);
                break;
            case LOCAL:
                pw.append("local ");
            case MEMBER_OR_STATIC:
                pw.append("innerclass ");
                unparseIdentifiers(icd.name, pw);
                pw.append(" ").append(icd.innerName);
                break;
        }
        pw.println();
    }

    private void unparseField(FieldDeclaration fd, PrintWriter pw) {
        pw.println();
        unparseAnnotations(fd.annotations, pw);
        appendIndent(pw);
        unparseModifiers(fd.modifiers, pw);
        pw.append(fd.type.toString()).append(" ").append(fd.name);
        if (!(fd.value instanceof NullLiteral)) {
            pw.append(" = ").append(fd.value.toString());
        }
        pw.println();
    }

    private void unparseMethod(MethodDeclaration md, PrintWriter pw) {
        pw.println();
        unparseAnnotations(md.annotations, pw);
        if ("<clinit>".equals(md.name)) {
            appendIndent(pw).append("static");
        } else {
            appendIndent(pw);
            unparseModifiers(md.modifiers, pw);
            if ("<init>".equals(md.name)) {
                pw.append("<init>");
            } else {
                pw.append(md.returnType.toString()).append(" ").append(md.name);
            }
            pw.append("(");
            unparseMethodParameters(md.parameterTypes, pw);
            pw.append(")");
            unparseMethodExceptions(md.exceptionTypes, pw);
            unparseMethodDefaultValue(md.defaultValue, pw);
        }

        if (hasModifier(md.modifiers, "abstract")) {
            pw.println();
            return;
        }

        pw.append(" {").println();
        push2();
        unparseStatements(md.body.statements, pw);
        pop2();
        appendIndent(pw).append("}").println();
    }

    private void unparseMethodParameters(List<Type> parameterTypes, PrintWriter pw) {
        if (parameterTypes.isEmpty()) return;
        Iterator<Type> it = parameterTypes.iterator();
        pw.append(it.next().toString());
        while (it.hasNext()) {
            pw.append(", ").append(it.next().toString());
        }
    }

    private void unparseMethodExceptions(List<ReferenceType> exceptionTypes, PrintWriter pw) {
        if (exceptionTypes.isEmpty()) return;
        pw.append(" throws ");
        Iterator<ReferenceType> it = exceptionTypes.iterator();
        pw.append(it.next().toString());
        while (it.hasNext()) {
            pw.append(", ").append(it.next().toString());
        }
    }

    private void unparseMethodDefaultValue(AnnotationValue defaultValue, PrintWriter pw) {
        if (defaultValue == null) return;
        pw.append(" default ").append(defaultValue.toString());
    }

    private void unparseStatements(List<Statement> statements, PrintWriter pw) {
        for (Statement statement : statements) {
            unparseStatement(statement, pw);
        }
    }

    private void unparseStatement(Statement statement, PrintWriter pw) {
        if (statement instanceof Insn) {
            unparseInsn((Insn) statement, pw);
        } else if (statement instanceof IntInsn) {
            unparseIntInsn((IntInsn) statement, pw);
        } else if (statement instanceof VarInsn) {
            unparseVarInsn((VarInsn) statement, pw);
        } else if (statement instanceof TypeInsn) {
            unparseTypeInsn((TypeInsn) statement, pw);
        } else if (statement instanceof FieldInsn) {
            unparseFieldInsn((FieldInsn) statement, pw);
        } else if (statement instanceof MethodInsn) {
            unparseMethodInsn((MethodInsn) statement, pw);
        } else if (statement instanceof InvokeDynamicInsn) {
            unparseInvokeDynamicInsn((InvokeDynamicInsn) statement, pw);
        } else if (statement instanceof JumpInsn) {
            unparseJumpInsn((JumpInsn) statement, pw);
        } else if (statement instanceof Label) {
            unparseLabelInsn((Label) statement, pw);
        } else if (statement instanceof LdcInsn) {
            unparseLdcInsn((LdcInsn) statement, pw);
        } else if (statement instanceof IincInsn) {
            unparseIincInsn((IincInsn) statement, pw);
        } else if (statement instanceof SwitchInsn) {
            unparseSwitchInsn((SwitchInsn) statement, pw);
        } else if (statement instanceof NewArrayInsn) {
            unparseNewArrayInsn((NewArrayInsn) statement, pw);
        } else if (statement instanceof MultiANewArrayInsn) {
            unparseMultiANewArrayInsn((MultiANewArrayInsn) statement, pw);
        } else if (statement instanceof Block) {
            unparseBlock((Block) statement, pw);
        } else if (statement instanceof LineNumber) {
            unparseLineNumber((LineNumber) statement, pw);
        } else if (statement instanceof LocalVariable) {
            unparseLocalVariable((LocalVariable) statement, pw);
        } else if (statement instanceof TryCatchBlock) {
            unparseTryCatchBlock((TryCatchBlock) statement, pw);
        } else {
            throw new IllegalArgumentException();
        }
    }

    private void unparseBlock(Block block, PrintWriter pw) {
        appendIndent(pw).append("{").println();
        push2();
        unparseStatements(block.statements, pw);
        pop2();
        appendIndent(pw).append("}").println();
    }

    private void unparseInsn(Insn insn, PrintWriter pw) {
        appendIndent(pw).append(insn.opcode).println();
    }

    private void unparseIntInsn(IntInsn intInsn, PrintWriter pw) {
        appendIndent(pw).append(intInsn.opcode).append(" ").append(intInsn.operand.toString()).println();
    }

    private void unparseVarInsn(VarInsn varInsn, PrintWriter pw) {
        appendIndent(pw).append(varInsn.opcode).append(" ").append(varInsn.var.toString()).println();
    }

    private void unparseTypeInsn(TypeInsn typeInsn, PrintWriter pw) {
        appendIndent(pw).append(typeInsn.opcode).append(" ").append(typeInsn.type.toString()).println();
    }

    private void unparseFieldInsn(FieldInsn fieldInsn, PrintWriter pw) {
        appendIndent(pw).append(fieldInsn.opcode);
        pw.append(" ").append(fieldInsn.owner.toString());
        pw.append(" ").append(fieldInsn.name);
        pw.append(" ").append(fieldInsn.type.toString());
        pw.println();
    }

    private void unparseMethodInsn(MethodInsn methodInsn, PrintWriter pw) {
        appendIndent(pw).append(methodInsn.opcode);
        pw.append(" ").append(methodInsn.owner.toString());
        pw.append(" ").append(methodInsn.name);
        pw.append(" ").append(methodInsn.methodType.toString());
        pw.println();
    }

    private void unparseInvokeDynamicInsn(InvokeDynamicInsn invokeDynamicInsn, PrintWriter pw) {
        appendIndent(pw).append(invokeDynamicInsn.opcode).append(" {").println();
        push();
        appendIndent(pw).append(invokeDynamicInsn.name).println();
        appendIndent(pw).append(invokeDynamicInsn.methodType.toString()).println();
        appendIndent(pw);
        unparseHandle(invokeDynamicInsn.bootstrapMethod, pw);
        pw.println();
        unparseBootstrapMethodArguments(invokeDynamicInsn.bootstrapMethodArguments, pw);
        pop();
        appendIndent(pw).append("}").println();
    }

    private void unparseJumpInsn(JumpInsn jumpInsn, PrintWriter pw) {
        appendIndent(pw).append(jumpInsn.opcode).append(" ").append(jumpInsn.label).println();
    }

    private void unparseLabelInsn(Label label, PrintWriter pw) {
        pop();
        appendIndent(pw).append(label.name).append(":").println();
        push();
    }

    private void unparseLdcInsn(LdcInsn ldcInsn, PrintWriter pw) {
        appendIndent(pw).append(ldcInsn.opcode).append(" ").append(ldcInsn.value.toString()).println();
    }

    private void unparseIincInsn(IincInsn iincInsn, PrintWriter pw) {
        appendIndent(pw).append(iincInsn.opcode);
        pw.append(" ").append(iincInsn.var.toString());
        pw.append(" ").append(iincInsn.increment.toString());
        pw.println();
    }

    private void unparseSwitchInsn(SwitchInsn switchInsn, PrintWriter pw) {
        appendIndent(pw).append("switch {").println();
        push();
        for (SwitchCase cas : switchInsn.cases) {
            appendIndent(pw).append(cas.key.toString()).append(": ").append(cas.label).println();
        }
        appendIndent(pw).append("default: ").append(switchInsn.dflt).println();
        pop();
        appendIndent(pw).append("}").println();
    }

    private void unparseNewArrayInsn(NewArrayInsn newArrayInsn, PrintWriter pw) {
        appendIndent(pw).append(newArrayInsn.opcode).append(" ").append(newArrayInsn.type.toString()).println();
    }

    private void unparseMultiANewArrayInsn(MultiANewArrayInsn multiANewArrayInsn, PrintWriter pw) {
        appendIndent(pw).append(multiANewArrayInsn.opcode);
        pw.append(" ").append(multiANewArrayInsn.type.toString());
        pw.append(" ").append(multiANewArrayInsn.numDimensions.toString());
        pw.println();
    }

    private void unparseLineNumber(LineNumber lineNumber, PrintWriter pw) {
        if (skipLineNumber) return;
        appendIndent(pw).append("line");
        pw.append(" ").append(lineNumber.line.toString());
        pw.append(" ").append(lineNumber.label);
        pw.println();
    }

    private void unparseLocalVariable(LocalVariable localVariable, PrintWriter pw) {
        if (skipLocalVariable) return;
        appendIndent(pw).append("var");
        pw.append(" ").append(localVariable.name);
        pw.append(" ").append(localVariable.type.toString());
        pw.append(" ").append(localVariable.start);
        pw.append(" ").append(localVariable.end);
        pw.append(" ").append(localVariable.index.toString());
        pw.println();
    }

    private void unparseTryCatchBlock(TryCatchBlock tryCatchBlock, PrintWriter pw) {
        appendIndent(pw).append("try");
        pw.append(" ").append(tryCatchBlock.start);
        pw.append(" ").append(tryCatchBlock.end);
        pw.append(" ").append(tryCatchBlock.handler);
        pw.append(" ").append(tryCatchBlock.type != null ? tryCatchBlock.type.toString() : "finally");
        pw.println();
    }

    private void unparseValue(Value value, PrintWriter pw) {
        if (value instanceof Literal) {
            pw.append(value.toString());
        } else if (value instanceof Type) {
            pw.append(value.toString());
        } else if (value instanceof MethodType) {
            pw.append(value.toString());
        } else if (value instanceof Handle) {
            unparseHandle((Handle) value, pw);
        } else if (value instanceof ConstantDynamic) {
            unparseConstantDynamic((ConstantDynamic) value, pw);
        }
    }

    private void unparseHandle(Handle handle, PrintWriter pw) {
        pw.append("Handle {").println();
        push();
        appendIndent(pw).append(handle.kind).println();
        appendIndent(pw).append(handle.owner.toString()).println();
        appendIndent(pw).append(handle.name).println();
        appendIndent(pw).append(handle.type.toString()).println();
        pop();
        appendIndent(pw).append("}");
    }

    private void unparseConstantDynamic(ConstantDynamic constantDynamic, PrintWriter pw) {
        pw.append("ConstantDynamic {").println();
        push();
        appendIndent(pw).append(constantDynamic.name).println();
        appendIndent(pw).append(constantDynamic.type.toString()).println();
        appendIndent(pw);
        unparseHandle(constantDynamic.bootstrapMethod, pw);
        pw.println();
        unparseBootstrapMethodArguments(constantDynamic.bootstrapMethodArguments, pw);
        pop();
        appendIndent(pw).append("}");
    }

    private void unparseBootstrapMethodArguments(List<Value> arguments, PrintWriter pw) {
        appendIndent(pw).append("{").println();
        push();
        if (!arguments.isEmpty()) {
            appendIndent(pw);
            Iterator<Value> it = arguments.iterator();
            unparseValue(it.next(), pw);
            while (it.hasNext()) {
                pw.append(",").println();
                appendIndent(pw);
                unparseValue(it.next(), pw);
            }
            pw.println();
        }
        pop();
        appendIndent(pw).append("}").println();
    }

    private boolean hasModifier(List<Modifier> modifiers, String keyword) {
        for (Modifier modifier : modifiers) {
            if (modifier.keyword.equals(keyword)) {
                return true;
            }
        }
        return false;
    }
}
