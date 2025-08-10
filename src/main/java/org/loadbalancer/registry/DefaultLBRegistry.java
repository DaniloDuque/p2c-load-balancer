package org.loadbalancer.registry;

import lombok.Builder;
import lombok.NonNull;
import org.core.HostMetadata;
import org.core.metric.MetricValue;

import java.util.Collection;
import java.util.concurrent.ConcurrentMap;

@Builder
public final class DefaultLBRegistry implements LBRegistry {
    private final ConcurrentMap<
            HostMetadata,
            Collection<MetricValue>
            > registryMap;
    private final Selector selector;

    @Override
    public HostMetadata getNextLoadBalancedHost() {
        return selector.getNextHost(registryMap);
    }

    @Override
    public void updateWorkerHost(
            @NonNull final HostMetadata hostMetadata,
            @NonNull final Collection<MetricValue> metricsValues) {
        registryMap.put(hostMetadata, metricsValues);
    }
}
