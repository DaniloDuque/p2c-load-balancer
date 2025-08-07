package org.core.metric;

import java.util.concurrent.atomic.AtomicLong;

public class ThroughputMetric implements CounterMetric {
    private final AtomicLong count = new AtomicLong(0);

    @Override
    public void increment() {
        count.incrementAndGet();
    }

    @Override
    public Long getValue() {
        return count.get();
    }

    @Override
    public void reset() {
        count.set(0);
    }
}
