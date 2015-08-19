package com.cloud.utils.rest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.Test;

public class ExecutionCounterTest {

    @Test
    public void testIncrementCounter() throws Exception {
        final ExecutionCounter executionCounter = new ExecutionCounter(-1);

        executionCounter.incrementExecutionCounter().incrementExecutionCounter();

        assertThat(executionCounter.getValue(), equalTo(2));
    }

    @Test
    public void testHasNotYetReachedTheExecutuionLimit() throws Exception {
        final ExecutionCounter executionCounter = new ExecutionCounter(2);

        executionCounter.incrementExecutionCounter();

        assertThat(executionCounter.hasReachedExecutionLimit(), equalTo(false));
    }

    @Test
    public void testHasAlreadyReachedTheExecutuionLimit() throws Exception {
        final ExecutionCounter executionCounter = new ExecutionCounter(2);

        executionCounter.incrementExecutionCounter().incrementExecutionCounter();

        assertThat(executionCounter.hasReachedExecutionLimit(), equalTo(true));
    }
}
