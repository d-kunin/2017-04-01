package ru.otus.kunin.dunit;

import com.google.common.base.Preconditions;
import ru.otus.kunin.dunit.annotation.After;
import ru.otus.kunin.dunit.annotation.Before;
import ru.otus.kunin.dunit.annotation.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class TestClass {

    private final Class<?> klazz;

    private final Constructor<Class<?>> constructor;

    private final List<TestMethod> testMethods;

    private final Optional<Method> before;

    private final Optional<Method> after;

    public TestClass(Class<?> klazz) {
        Preconditions.checkNotNull(klazz);
        this.klazz = klazz;

        try {
            this.constructor = (Constructor<Class<?>>) klazz.getDeclaredConstructor();
            this.constructor.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(klazz + " must have parameterless constructor");
        }

        this.testMethods = takeMethodsAnnotatedWith(Test.class)
                .map(TestMethod::new)
                .collect(toList());
        this.before= takeMethodsAnnotatedWith(Before.class)
                .findFirst(); // TODO what to do if many
        this.after= takeMethodsAnnotatedWith(After.class)
                .findFirst(); // TODO what to do if many
    }

    public void runBefore(Object instance) {
        before.ifPresent(method -> {
            try {
                method.invoke(instance);
            } catch (IllegalAccessException|InvocationTargetException e) {
                throw new RuntimeException("Cant run @Before: " + e);
            }
        });
    }

    public void runAfter(Object instance) {
        after.ifPresent(method -> {
            try {
                method.invoke(instance);
            } catch (IllegalAccessException|InvocationTargetException e) {
                throw new RuntimeException("Cant run @After: " + e);
            }
        });
    }

    public Object newInstance() {
        try {
            return constructor.newInstance();
        } catch (InstantiationException|IllegalAccessException|InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public List<TestMethod> testMethods() {
        return testMethods;
    }

    private Stream<Method> takeMethodsAnnotatedWith(Class<? extends Annotation> desiredAnnotationClass) {
        return Stream.of(klazz.getDeclaredMethods())
                .filter(method -> method.getParameterCount() == 0)
                .filter(method ->
                        Stream.of(method.getDeclaredAnnotations())
                                .anyMatch(annotation -> annotation.annotationType() == desiredAnnotationClass));
    }
}
