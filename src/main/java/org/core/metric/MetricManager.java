package org.core.metric;

import lombok.Builder;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.worker.client.MetricClient;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Optional;
import java.util.ArrayList;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Log4j2
@Builder
public final class MetricManager {
    private final TimeUnit timeUnit;
    private final Integer initialDelay;
    private final Integer delay;
    private final MetricClient client;
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

    @PostConstruct
    public void start() {
        scheduler.scheduleWithFixedDelay(
                this::collectAndSendMetrics,
                initialDelay, delay, timeUnit
        );
    }

    private Collection<Metric<?>> collectMetrics() {
        val result = new ArrayList<Metric<?>>();
        result.addAll(metrics.values());
        result.addAll(updatableMetrics.values());
        return result;
    }

    private void collectAndSendMetrics() {
        val metricValues = collectMetrics();
        client.send(metricValues);
    }

    public static final class MetricManagerBuilder {
        public MetricManager build() {
            MetricManager manager = new MetricManager(
                    timeUnit,
                    initialDelay,
                    delay,
                    client,
                    scheduler
            );
            manager.start();
            return manager;
        }
    }

}
