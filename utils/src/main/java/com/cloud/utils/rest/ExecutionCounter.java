package com.cloud.utils.rest;

import java.util.concurrent.atomic.AtomicInteger;

public class ExecutionCounter {

    private final int executionLimit;
    private final AtomicInteger executionCount = new AtomicInteger(0);

    public ExecutionCounter(final int executionLimit) {
        this.executionLimit = executionLimit;
    }

    public ExecutionCounter resetExecutionCounter() {
        executionCount.set(0);
        return this;
    }

    public boolean hasReachedExecutionLimit() {
        return executionCount.get() >= executionLimit;
    }

    public ExecutionCounter incrementExecutionCounter() {
        executionCount.incrementAndGet();
        return this;
    }

    public int getValue() {
        return executionCount.get();
    }
}
