package org.loadbalancer.registry;

import org.core.metric.MetricValue;

import java.util.Collection;

public interface HostScorer {
    double score(Collection<MetricValue> metrics);
}
