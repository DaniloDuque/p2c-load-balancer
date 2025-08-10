package org.worker.client;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.core.HostMetadata;
import org.core.metric.Metric;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.Collection;
import java.util.stream.Collectors;

@Log4j2
@NoArgsConstructor
public final class DefaultMetricRequestAdapter implements MetricRequestAdapter {
    private static final String METRIC_PATH = "metrics";
    private static final String CONTENT_TYPE_NAME = "Content-Type";
    private static final String CONTENT_TYPE_VALUE = "application/json";

    private String serializeMetrics(
            @NonNull final Collection<Metric<?>> metrics) {
        return metrics.stream()
                .map(metric -> metric.getValue().toString())
                .collect(Collectors.joining(","));
    }

    @Override
    public HttpRequest adapt(
            @NonNull final HostMetadata hostMetadata,
            @NonNull final Collection<Metric<?>> metrics) {
        val uri = URI.create(
                String.format(
                        "http://%s:%d/%s",
                        hostMetadata.host(),
                        hostMetadata.port(),
                        METRIC_PATH
                )
        );
        log.info(serializeMetrics(metrics));
        return HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(
                        serializeMetrics(metrics))
                )
                .header(CONTENT_TYPE_NAME, CONTENT_TYPE_VALUE)
                .build();
    }
}
