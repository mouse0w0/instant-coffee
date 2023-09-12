package com.github.mouse0w0.instantcoffee.model;

import com.github.mouse0w0.instantcoffee.Location;

import java.util.ArrayList;
import java.util.List;

public class ClassDeclaration extends Located {
    public IntegerLiteral version;
    public Annotation[] annotations;
    public Modifier[] modifiers;
    public String[] identifiers;
    public TypeParameter[] typeParameters;
    public ReferenceType superclass;
    public ReferenceType[] interfaces;
    public SourceDeclaration source;
    public List<InnerClassDeclaration> innerClasses;
    public List<FieldDeclaration> fields;
    public List<MethodDeclaration> methods;

    public ClassDeclaration(Location location, Annotation[] annotations, Modifier[] modifiers, String[] identifiers, TypeParameter[] typeParameters, ReferenceType superclass, ReferenceType[] interfaces) {
        super(location);
        this.annotations = annotations;
        this.modifiers = modifiers;
        this.identifiers = identifiers;
        this.typeParameters = typeParameters;
        this.superclass = superclass;
        this.interfaces = interfaces;
        this.innerClasses = new ArrayList<>();
        this.fields = new ArrayList<>();
        this.methods = new ArrayList<>();
    }

    public ClassDeclaration(Location location, Annotation[] annotations, Modifier[] modifiers, String[] identifiers, TypeParameter[] typeParameters, ReferenceType superclass, ReferenceType[] interfaces, SourceDeclaration source, List<InnerClassDeclaration> innerClasses, List<FieldDeclaration> fields, List<MethodDeclaration> methods) {
        super(location);
        this.annotations = annotations;
        this.modifiers = modifiers;
        this.identifiers = identifiers;
        this.typeParameters = typeParameters;
        this.superclass = superclass;
        this.interfaces = interfaces;
        this.source = source;
        this.innerClasses = innerClasses;
        this.fields = fields;
        this.methods = methods;
    }
}
