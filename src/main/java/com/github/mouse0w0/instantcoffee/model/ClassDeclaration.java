package com.github.mouse0w0.instantcoffee.model;

import com.github.mouse0w0.instantcoffee.Location;

import java.util.ArrayList;
import java.util.List;

public class ClassDeclaration extends Located {
    public IntegerLiteral version;
    public List<Annotation> annotations;
    public List<Modifier> modifiers;
    public List<String> identifiers;
    public ReferenceType superclass;
    public List<ReferenceType> interfaces;
    public StringLiteral source;
    public ReferenceType nestHost;
    public List<ReferenceType> nestMembers;
    public List<InnerClassDeclaration> innerClasses;
    public List<FieldDeclaration> fields;
    public List<MethodDeclaration> methods;

    public ClassDeclaration(Location location) {
        super(location);
        this.annotations = new ArrayList<>();
        this.modifiers = new ArrayList<>();
        this.identifiers = new ArrayList<>();
        this.interfaces = new ArrayList<>();
        this.nestMembers = new ArrayList<>();
        this.innerClasses = new ArrayList<>();
        this.fields = new ArrayList<>();
        this.methods = new ArrayList<>();
    }

    public ClassDeclaration(Location location, List<Annotation> annotations, List<Modifier> modifiers, List<String> identifiers, ReferenceType superclass, List<ReferenceType> interfaces) {
        super(location);
        this.annotations = annotations;
        this.modifiers = modifiers;
        this.identifiers = identifiers;
        this.superclass = superclass;
        this.interfaces = interfaces;
        this.nestMembers = new ArrayList<>();
        this.innerClasses = new ArrayList<>();
        this.fields = new ArrayList<>();
        this.methods = new ArrayList<>();
    }
}
