package org.loadbalancer.registry;

import lombok.Builder;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.core.HostMetadata;
import org.core.metric.HostStatus;
import org.core.metric.MetricValue;

import java.util.Collection;
import java.util.concurrent.ConcurrentMap;

@Log4j2
@Builder
public final class DefaultLBRegistry implements LBRegistry {
    private final ConcurrentMap<
            HostMetadata,
            Collection<MetricValue>
            > registryMap;
    private final Selector selector;

    @Override
    public HostMetadata getNextLoadBalancedHost() {
        log.info("registry map has " + registryMap.size() + " entries");
        return selector.getNextHost(registryMap);
    }

    @Override
    public void updateWorkerHost(@NonNull final HostStatus hostStatus) {
        registryMap.put(hostStatus.hostMetadata(), hostStatus.metrics());
    }
}
