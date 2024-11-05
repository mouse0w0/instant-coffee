package com.github.mouse0w0.instantcoffee.model;

import com.github.mouse0w0.instantcoffee.Location;
import com.github.mouse0w0.instantcoffee.model.statement.Block;

public class MethodDeclaration extends Located {
    public static final MethodDeclaration[] EMPTY_ARRAY = {};

    public Annotation[] annotations;
    public Modifier[] modifiers;
    public Type returnType;
    public String name;
    public Type[] parameterTypes;
    public ReferenceType[] exceptionTypes;
    public AnnotationValue defaultValue;

    public Block body;

    public MethodDeclaration(Location location, Annotation[] annotations, Modifier[] modifiers, Type returnType, String name, Type[] parameterTypes, ReferenceType[] exceptionTypes, AnnotationValue defaultValue) {
        super(location);
        this.annotations = annotations;
        this.modifiers = modifiers;
        this.returnType = returnType;
        this.name = name;
        this.parameterTypes = parameterTypes;
        this.exceptionTypes = exceptionTypes;
        this.defaultValue = defaultValue;
    }

    public MethodDeclaration(Location location, Annotation[] annotations, Modifier[] modifiers, Type returnType, String name, Type[] parameterTypes, ReferenceType[] exceptionTypes, AnnotationValue defaultValue, Block body) {
        super(location);
        this.annotations = annotations;
        this.modifiers = modifiers;
        this.returnType = returnType;
        this.name = name;
        this.parameterTypes = parameterTypes;
        this.exceptionTypes = exceptionTypes;
        this.defaultValue = defaultValue;
        this.body = body;
    }
}
