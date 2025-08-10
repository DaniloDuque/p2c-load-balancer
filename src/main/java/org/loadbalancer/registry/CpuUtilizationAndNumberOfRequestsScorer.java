package org.loadbalancer.registry;

import lombok.NonNull;
import lombok.val;
import org.core.metric.MetricName;
import org.core.metric.MetricValue;

import java.util.Collection;

public final class CpuUtilizationAndNumberOfRequestsScorer
        implements HostScorer {
    @Override
    public double score(
            @NonNull final Collection<MetricValue> metrics) {
        val cpuUtilization = metrics.stream()
                .filter(m -> m.name().equals(MetricName.CPU_UTILIZATION))
                .mapToDouble(m -> (Double) m.value())
                .findFirst()
                .orElse(0.0);

        val requestCount = metrics.stream()
                .filter(m -> m.name().equals(MetricName.NUMBER_OF_REQUESTS))
                .mapToDouble(m -> (Double) m.value())
                .findFirst()
                .orElse(0.0);

        return 1 / (cpuUtilization * requestCount + 1);
    }
}
