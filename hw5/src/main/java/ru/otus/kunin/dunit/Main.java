package ru.otus.kunin.dunit;

import com.google.common.collect.Lists;
import ru.otus.kunin.dunit.annotation.Before;
import ru.otus.kunin.dunit.annotation.Test;
import ru.otus.kunin.dunit.assertion.Assert;

import java.util.Optional;

public class Main {

    /**
     * This class runs tests in {@link SampleTestClass}
     * and verifies the result.
     */
    public static class SelfTest {

        Report underTest;

        @Before
        void setUp() {
            final TestRunner testRunner = new TestRunner();
            SampleTestClass.numberOfSetUps = 0;
            SampleTestClass.numberOfTearDowns = 0;
            underTest = testRunner.run(Lists.newArrayList(SampleTestClass.class));
        }

        @Test
        void testSixTestsRanTotal() {
            Assert.assertEqual(6, underTest.total);
        }

        @Test
        void testFourTestsSucceed() {
            Assert.assertEqual(4, underTest.success);
        }

        @Test
        void testOneTestsFailed() {
            Assert.assertEqual(1, underTest.failed);
            final Optional<TestResult> failingTest = underTest.testResults.stream()
                    .filter(testResult -> testResult.testMethod.getName().equals("failingTest")).findFirst();
            Assert.assertTrue(failingTest.isPresent());
            Assert.assertEqual(TestResult.Status.FAILED, failingTest.get().status);
        }

        @Test
        void testOneTestWithError() {
            Assert.assertEqual(1, underTest.error);
            final Optional<TestResult> errorTest = underTest.testResults.stream()
                    .filter(testResult -> testResult.testMethod.getName().equals("throwingTest")).findFirst();
            Assert.assertTrue(errorTest.isPresent());
            Assert.assertEqual(TestResult.Status.ERROR, errorTest.get().status);
        }

        @Test
        void testNumberOfSetUpsAndTearDowns() {
            Assert.assertEqual(6, SampleTestClass.numberOfSetUps);
            Assert.assertEqual(6, SampleTestClass.numberOfTearDowns);
        }

    }

    public static void main(String[] args) {
        System.out.println(">>>dUnit is stating self test");
        final TestRunner testRunner = new TestRunner();
        System.out.println(testRunner.run(Lists.newArrayList(SelfTest.class)));
        System.out.println("<<<self testing done");
    }
}
