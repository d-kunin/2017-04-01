package ru.otus.kunin.dunit;

import com.google.common.collect.Lists;

public class Main {

    public static void main(String[] args) {
        System.out.println("dUnit");
        final TestRunner testRunner = new TestRunner();
        final Report report = testRunner.run(Lists.newArrayList(SampleTestClass.class));
        System.out.println(report.toString());
    }
}
