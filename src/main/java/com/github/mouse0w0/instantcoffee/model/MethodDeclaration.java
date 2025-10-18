package com.github.mouse0w0.instantcoffee.model;

import com.github.mouse0w0.instantcoffee.Location;
import com.github.mouse0w0.instantcoffee.model.statement.Block;

import java.util.List;

public class MethodDeclaration extends Located {
    public List<Annotation> annotations;
    public List<Modifier> modifiers;
    public Type returnType;
    public String name;
    public List<Type> parameterTypes;
    public List<ReferenceType> exceptionTypes;
    public AnnotationValue defaultValue;

    public Block body;

    public MethodDeclaration(Location location, List<Annotation> annotations, List<Modifier> modifiers, Type returnType, String name, List<Type> parameterTypes, List<ReferenceType> exceptionTypes, AnnotationValue defaultValue) {
        super(location);
        this.annotations = annotations;
        this.modifiers = modifiers;
        this.returnType = returnType;
        this.name = name;
        this.parameterTypes = parameterTypes;
        this.exceptionTypes = exceptionTypes;
        this.defaultValue = defaultValue;
    }

    public MethodDeclaration(Location location, List<Annotation> annotations, List<Modifier> modifiers, Type returnType, String name, List<Type> parameterTypes, List<ReferenceType> exceptionTypes, AnnotationValue defaultValue, Block body) {
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
