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
                    testClass.runBefore(testClassInstance);

                    System.out.println("running test:" + testMethod);
                    testMethod.invoke(testClassInstance);

                    testClass.runAfter(testClassInstance);

                } catch (AssertionError assertionError) {

                } catch (Throwable throwable) {

                }
            });
        });

        return new Report();
    }

}
