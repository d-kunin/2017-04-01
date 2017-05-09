package ru.otus.kunin.dunit;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import ru.otus.kunin.dunit.annotation.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class TestClass {

    private final Class<?> klazz;

    private final Constructor<Class<?>> constructor;

    private final List<TestMethod> testMethods;

    public TestClass(Class<?> klazz) {
        Preconditions.checkNotNull(klazz);
        this.klazz = klazz;

        try {
            this.constructor = (Constructor<Class<?>>) klazz.getDeclaredConstructor();
            this.constructor.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(klazz + " must have parameterless constructor");
        }

        this.testMethods = Stream.of(klazz.getDeclaredMethods())
                .filter(method -> method.getParameterCount() == 0)
                .filter(method ->
                        Stream.of(method.getDeclaredAnnotations())
                                .anyMatch(annotation -> annotation instanceof Test))
                .map(TestMethod::new)
                .collect(toList());
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

}
