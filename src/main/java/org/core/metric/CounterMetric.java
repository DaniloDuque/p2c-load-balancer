package org.core.metric;

public interface CounterMetric extends Metric<Long> {
    void increment();
}
