package ru.otus.kunin.dunit.assertion;

import java.util.Objects;

public class Assert {

    public static <T> void assertEqual(T expected, T actual) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError("expected: " + expected + ", but was: " + actual);
        }
    }

    public static void assertTrue(boolean condition) {
        if (!condition) {
            throw new AssertionError("Condition failed");
        }
    }

    public static void fail(String message) {
        throw new AssertionError(message);
    }

    public static void assertNotNull(Object ref) {
        if (null == ref) {
            throw new AssertionError("Not null expected");
        }
    }

}
