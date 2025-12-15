package com.github.mouse0w0.instantcoffee;

import org.junit.jupiter.api.Test;

public class ClassTypeParameterTest {
    @Test
    public void test() {
        Utils.validate(ClassTypeParameter.class, false, true);
    }

    // signature <T:Ljava/lang/Object;U:Lcom/github/mouse0w0/instantcoffee/ClassTypeParameterTest$A;V::Lcom/github/mouse0w0/instantcoffee/ClassTypeParameterTest$B;W:Lcom/github/mouse0w0/instantcoffee/ClassTypeParameterTest$A;:Lcom/github/mouse0w0/instantcoffee/ClassTypeParameterTest$B;X::Lcom/github/mouse0w0/instantcoffee/ClassTypeParameterTest$B;:Lcom/github/mouse0w0/instantcoffee/ClassTypeParameterTest$C;Y:Lcom/github/mouse0w0/instantcoffee/ClassTypeParameterTest$A;:Lcom/github/mouse0w0/instantcoffee/ClassTypeParameterTest$B;:Lcom/github/mouse0w0/instantcoffee/ClassTypeParameterTest$C;>Ljava/lang/Object;
    public static class ClassTypeParameter<
            T,
            U extends A,
            V extends B,
            W extends A & B,
            X extends B & C,
            Y extends A & B & C
            > {
    }

    public static class A {
    }

    public interface B {
    }

    public interface C {
    }
}
