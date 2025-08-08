package org.core.metric;

public interface UpdatableMetric<T> extends Metric<T> {
    void update(T value);
}
