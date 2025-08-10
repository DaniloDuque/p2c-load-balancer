package org.loadbalancer.registry;

import org.core.HostMetadata;
import org.core.metric.MetricValue;

import java.util.Collection;

public interface LBRegistry {
    HostMetadata getNextLoadBalancedHost();
    void updateWorkerHost(
            HostMetadata hostMetadata,
            Collection<MetricValue> metricsValues
    );

}
