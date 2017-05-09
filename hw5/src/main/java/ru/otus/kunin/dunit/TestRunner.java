package ru.otus.kunin.dunit;

import ru.otus.kunin.dunit.assertion.AssertionError;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class TestRunner {

    public Report run(List<Class<?>> classes) {

        final List<TestClass> testClasses = classes.stream()
                .map(TestClass::new)
                .collect(toList());

        testClasses.forEach(testClass -> {
            testClass.testMethods().forEach(testMethod -> {
                try {
                    final Object testClassInstance = testClass.newInstance();
                    // TODO before
                    System.out.println("calling:" + testMethod);
                    testMethod.invoke(testClassInstance);
                    // TODO after
                } catch (AssertionError assertionError) {

                } catch (Throwable throwable) {

                }
            });
        });

        return new Report();
    }

}
