package ru.otus.kunin.dunit;

import com.google.common.base.Preconditions;
import ru.otus.kunin.dunit.annotation.After;
import ru.otus.kunin.dunit.annotation.Before;
import ru.otus.kunin.dunit.annotation.Test;
import ru.otus.kunin.dunit.assertion.Assert;

/**
 * This class is a sample that will be used to test
 * the test framework itself.
 */
public class SampleTestClass {

    boolean objectIsSetUp = false;

    @Before
    void setUp() {
        Preconditions.checkArgument(!objectIsSetUp);
        objectIsSetUp = true;
    }

    @Test
    void passingTest() {
        Assert.assertTrue(true);
    }

    @Test
    void slowTest() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException ignored) {
        }
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
        Preconditions.checkArgument(objectIsSetUp);
        objectIsSetUp = false;
    }
}
