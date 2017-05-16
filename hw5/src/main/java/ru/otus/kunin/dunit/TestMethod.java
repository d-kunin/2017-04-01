package ru.otus.kunin.dunit;

import com.google.common.base.Preconditions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TestMethod {

    private final Method method;

    public TestMethod(Method method) {
        this.method = Preconditions.checkNotNull(method);
    }

    public void invoke(Object instance) throws Throwable {
        try {
            method.invoke(instance);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Cant call test method", e);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

    @Override
    public String toString() {
        return method.getName();
    }
}
