package org.core.metric;

import lombok.RequiredArgsConstructor;
import org.core.client.Client;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@RequiredArgsConstructor
public class MetricManager {
    private final Client client;
    private final ConcurrentMap<MetricName, Metric<?>> metrics = new ConcurrentHashMap<>();

    public <T extends Metric<?>> T register(MetricName name, T metric) {
        metrics.put(name, metric);
        return metric;
    }

    public Metric<?> getMetric(MetricName name) {
        return metrics.get(name);
    }

}