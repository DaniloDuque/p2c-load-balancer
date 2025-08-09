package org.core.client;

import lombok.Builder;
import lombok.NonNull;
import org.core.metric.Metric;

import java.util.Collection;

@Builder
public class DefaultMetricClient implements MetricClient {
    private final HostMetadata hostMetadata;

    @Override
    public void send(@NonNull final Collection<Metric<?>> metrics) {

    }
}
