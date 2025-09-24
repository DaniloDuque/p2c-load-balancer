package org.loadbalancer.registry;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.core.metric.MetricName;
import org.core.metric.MetricValue;

import java.util.Collection;

@Slf4j
public final class CpuUtilizationAndNumberOfRequestsScorer
        implements HostScorer {
    private static final double DEFAULT_REQUEST_COUNT = 1e10;
    @Override
    public double score(@NonNull final Collection<MetricValue> metrics) {
        double cpuUtilization = 1;
        double requestCount = DEFAULT_REQUEST_COUNT;

        for (MetricValue metric : metrics) {
            double parseDouble = Double.parseDouble(metric.value().toString());
            if (metric.name() == MetricName.CPU_UTILIZATION) {
                cpuUtilization = parseDouble;
            } else if (metric.name() == MetricName.NUMBER_OF_REQUESTS) {
                requestCount = parseDouble;
            }
        }

        return 1.0 / (cpuUtilization * requestCount + 1);
    }

}
