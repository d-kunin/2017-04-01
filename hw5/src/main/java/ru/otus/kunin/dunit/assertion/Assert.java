package ru.otus.kunin.dunit.assertion;

import java.util.Objects;

public class Assert {

    // TODO(dima) add versions with messages

    public static <T> void assertEqual(T expected, T actual) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError();
        }
    }

    public static void assertTrue(boolean condition) {
        if (!condition) {
            throw new AssertionError();
        }
    }

    public static void fail() {
        throw new AssertionError();
    }

    public static void assertNotNull(Object ref) {
        if (null == ref) {
            throw new AssertionError();
        }
    }

}
