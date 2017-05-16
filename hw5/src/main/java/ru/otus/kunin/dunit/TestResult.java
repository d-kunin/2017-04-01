package ru.otus.kunin.dunit;

public class TestResult {

    public enum Status {
        DID_NOT_RUN,
        OK,
        FAILED,
        ERROR
    }

    public final Status status;
    public final long durationMs;
    public final TestMethod testMethod;
    public final Throwable throwable;

    private TestResult(Status status, long durationMs, TestMethod testMethod, Throwable throwable) {
        this.status = status;
        this.durationMs = durationMs;
        this.testMethod = testMethod;
        this.throwable = throwable;
    }

    public static class Builder {

        private Status status;
        private long durationMs;
        private TestMethod testMethod;
        private Throwable throwable;

        public Builder() {
            status = Status.DID_NOT_RUN;
            durationMs = 0;
            testMethod = null;
            throwable = null;
        }

        public Builder setStatus(Status status) {
            this.status = status;
            return this;
        }

        public Builder setDurationMs(long durationMs) {
            this.durationMs = durationMs;
            return this;
        }

        public Builder setTestMethod(TestMethod testMethod) {
            this.testMethod = testMethod;
            return this;
        }

        public Builder setThrowable(Throwable throwable) {
            this.throwable = throwable;
            return this;
        }

        public TestResult build() {
            return new TestResult(status, durationMs, testMethod, throwable);
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TestResult{");
        sb.append("status=").append(status);
        sb.append(", durationMs=").append(durationMs);
        sb.append(", testMethod=").append(testMethod);
        sb.append(", throwable=").append(throwable);
        sb.append('}');
        return sb.toString();
    }
}
