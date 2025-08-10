package org.loadbalancer.registry;

import org.core.HostMetadata;
import org.core.metric.MetricValue;

import java.util.Collection;
import java.util.concurrent.ConcurrentMap;

public interface Selector {
    HostMetadata getNextHost(
            ConcurrentMap<
                    HostMetadata,
                    Collection<MetricValue>
                    > workerHostMap
    );
}
