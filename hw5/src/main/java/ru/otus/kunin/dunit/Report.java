package ru.otus.kunin.dunit;

import com.google.common.base.Joiner;

import java.util.List;

public class Report {

    public final TestResult.Status status;

    public final int success;
    public final int failed;
    public final int error;
    public final int total;

    public final List<TestResult> testResults;

    public Report(List<TestResult> testResults) {
        this.testResults = testResults;
        total = testResults.size();
        success = (int) testResults.stream().filter(testResult -> testResult.status == TestResult.Status.OK).count();
        failed = (int) testResults.stream().filter(testResult -> testResult.status == TestResult.Status.FAILED).count();
        error = (int) testResults.stream().filter(testResult -> testResult.status == TestResult.Status.ERROR).count();
        status = success == total ? TestResult.Status.OK : TestResult.Status.FAILED;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Report{");
        sb.append("status=").append(status);
        sb.append(", success=").append(success);
        sb.append(", failed=").append(failed);
        sb.append(", error=").append(error);
        sb.append(", total=").append(total);
        sb.append(", testResults=\n").append(Joiner.on("\n").join(testResults));
        sb.append("\n}");
        return sb.toString();
    }
}
