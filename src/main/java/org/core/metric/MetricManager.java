package org.core.metric;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.core.client.Client;

import java.util.Optional;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;

@RequiredArgsConstructor
public final class MetricManager {
    private final Client client;
    private final ConcurrentMap<MetricName, Metric<?>> metrics =
            new ConcurrentHashMap<>();
    private final ConcurrentMap<MetricName, UpdatableMetric<?>>
            updatableMetrics = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler;

    public <T extends Metric<?>> void register(
            @NonNull final MetricName name, @NonNull final T metric) {
        metrics.put(name, metric);
    }

    public <T extends UpdatableMetric<?>> void register(
            @NonNull final MetricName name, @NonNull final T metric) {
        updatableMetrics.put(name, metric);
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<UpdatableMetric<T>> getMetric(
            @NonNull final MetricName name) {
        return Optional.ofNullable(
                (UpdatableMetric<T>) updatableMetrics.get(name)
        );
    }

}
