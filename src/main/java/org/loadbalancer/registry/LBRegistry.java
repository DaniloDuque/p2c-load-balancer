package org.loadbalancer.registry;

import org.core.HostMetadata;
import org.core.metric.HostStatus;

public interface LBRegistry {
    HostMetadata getNextLoadBalancedHost();
    void updateWorkerHost(HostStatus hostStatus);

}
