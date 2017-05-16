package ru.otus.kunin.dunit;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.util.stream.Collectors.toList;

public class TestRunner {

    public Report run(List<Class<?>> classes) {

        final List<TestClass> testClasses = classes.stream()
                .map(TestClass::new)
                .collect(toList());

        final ArrayList<TestResult> testResults = Lists.newArrayList();

        testClasses.forEach(testClass ->
                testClass.testMethods().forEach(testMethod -> {
            final TestResult.Builder resultBuilder = new TestResult.Builder();
            resultBuilder
                    .setStatus(TestResult.Status.DID_NOT_RUN)
                    .setTestMethod(testMethod);
            try {
                final Object testClassInstance = testClass.newInstance();
                final long startTimeNs = System.nanoTime();
                try {
                    testClass.runBefore(testClassInstance);
                    testMethod.invoke(testClassInstance);
                    resultBuilder.setStatus(TestResult.Status.OK);
                } finally {
                    testClass.runAfter(testClassInstance);
                    final long durationNs = System.nanoTime() - startTimeNs;
                    resultBuilder.setDurationMs(TimeUnit.NANOSECONDS.toMillis(durationNs));
                }
            } catch (AssertionError assertionError) {
                resultBuilder.setStatus(TestResult.Status.FAILED);
                resultBuilder.setThrowable(assertionError);
            } catch (Throwable throwable) {
                resultBuilder.setStatus(TestResult.Status.ERROR);
                resultBuilder.setThrowable(throwable);
            }
            testResults.add(resultBuilder.build());
        }));

        return new Report(testResults);
    }

}
