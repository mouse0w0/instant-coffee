package com.github.mouse0w0.instantcoffee.model;

import com.github.mouse0w0.instantcoffee.Location;
import com.github.mouse0w0.instantcoffee.model.insn.BaseInsn;

import java.util.ArrayList;
import java.util.List;

public class MethodDeclaration extends Located {
    public static final MethodDeclaration[] EMPTY_ARRAY = {};

    public Annotation[] annotations;
    public Modifier[] modifiers;
    public Type returnType;
    public String name;
    public Type[] parameterTypes;
    public ReferenceType[] exceptionTypes;
    public AnnotationValue defaultValue;

    public List<BaseInsn> instructions;
    public List<LocalVariable> localVariables;
    public List<TryCatchBlock> tryCatchBlocks;

    public MethodDeclaration(Location location, Annotation[] annotations, Modifier[] modifiers, Type returnType, String name, Type[] parameterTypes, ReferenceType[] exceptionTypes, AnnotationValue defaultValue) {
        super(location);
        this.annotations = annotations;
        this.modifiers = modifiers;
        this.returnType = returnType;
        this.name = name;
        this.parameterTypes = parameterTypes;
        this.exceptionTypes = exceptionTypes;
        this.defaultValue = defaultValue;
        this.instructions = new ArrayList<>();
        this.localVariables = new ArrayList<>();
        this.tryCatchBlocks = new ArrayList<>();
    }

    public MethodDeclaration(Location location, Annotation[] annotations, Modifier[] modifiers, Type returnType, String name, Type[] parameterTypes, ReferenceType[] exceptionTypes, AnnotationValue defaultValue, List<BaseInsn> instructions, List<LocalVariable> localVariables, List<TryCatchBlock> tryCatchBlocks) {
        super(location);
        this.annotations = annotations;
        this.modifiers = modifiers;
        this.returnType = returnType;
        this.name = name;
        this.parameterTypes = parameterTypes;
        this.exceptionTypes = exceptionTypes;
        this.defaultValue = defaultValue;
        this.instructions = instructions;
        this.localVariables = localVariables;
        this.tryCatchBlocks = tryCatchBlocks;
    }
}
