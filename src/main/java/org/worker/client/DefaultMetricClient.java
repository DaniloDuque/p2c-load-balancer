package org.worker.client;

import lombok.Builder;
import lombok.NonNull;
import lombok.val;
import org.core.HostMetadata;
import org.core.metric.Metric;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.Collection;

@Builder
public final class DefaultMetricClient implements MetricClient {
    private final MetricRequestAdapter adapter;
    private final HostMetadata hostMetadata;
    private final HttpClient client;

    @Override
    public void send(@NonNull final Collection<Metric<?>> metrics) {
        val request = adapter.adapt(hostMetadata, metrics);
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }
}
