package ru.otus.kunin.dunit;

import com.google.common.collect.Lists;
import com.sun.source.tree.AssertTree;
import ru.otus.kunin.dunit.annotation.After;
import ru.otus.kunin.dunit.annotation.Before;
import ru.otus.kunin.dunit.annotation.Test;
import ru.otus.kunin.dunit.assertion.Assert;

public class Main {

    public static class SampleTestClass {

        String resource = null;

        @Before
        void setUp() {
            resource = "The string";
        }

        @Test
        void passingTest() {
            Assert.assertTrue(true);
        }

        @Test
        void failingTest() {
            Assert.fail();
        }

        @Test
        void throwingTest() {
            throw new RuntimeException("eat that");
        }

        @Test
        void testWithNotNull() {
            Assert.assertNotNull(new Object());
        }

        @Test
        void equalityTest() {
            Assert.assertEqual("shilo", new String("shilo"));
        }

        @After
        void tearDown() {
            // To make sure set up was called
            resource.length();
            resource = null;
        }
    }

    public static void main(String[] args) {
        System.out.println("dUnit");
        final TestRunner testRunner = new TestRunner();
        final Report report = testRunner.run(Lists.newArrayList(SampleTestClass.class));
        System.out.println(report.toString());
    }
}
