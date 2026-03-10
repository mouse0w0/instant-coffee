package com.github.mouse0w0.instantcoffee;

import com.github.mouse0w0.instantcoffee.model.ReferenceType;
import com.github.mouse0w0.instantcoffee.model.Type;

interface Context {
    boolean isTypeVariable(ReferenceType type);

    Type getRawType(Type type);
}
