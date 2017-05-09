package ru.otus.kunin.dunit;

import com.google.common.base.Preconditions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TestMethod {

    private final Method method;

    public TestMethod(Method method) {
        this.method = Preconditions.checkNotNull(method);
    }

    public void invoke(Object instance) {
        try {
            method.invoke(instance);
        } catch (IllegalAccessException|InvocationTargetException e) {
            throw new RuntimeException("Cant call test method", e);
        }
    }

}
