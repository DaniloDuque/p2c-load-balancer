package org.core.client;

import org.core.metric.Metric;

import java.util.Collection;

public interface MetricClient {
    void send(Collection<Metric<?>> metrics);
}
