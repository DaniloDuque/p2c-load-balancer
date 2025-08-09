package org.core.metric;

public interface Metric<T> {
    T getValue();

    void reset();
}
