package org.core.metric;

import lombok.NonNull;
import lombok.val;
import org.core.HostMetadata;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.stream.Collectors;

public record HostStatus(HostMetadata hostMetadata,
                         Collection<MetricValue> metrics) {

    private String serializeMetrics(
            @NonNull final Collection<MetricValue> metrics) {
        return metrics.stream()
                .map(MetricValue::toString)
                .collect(Collectors.joining(","));
    }

    private static Collection<MetricValue> deserializeMetrics(
            @NonNull final String serializedMetrics) {
        return java.util.Arrays.stream(serializedMetrics.split(","))
                .map(metricStr -> new MetricValue(metricStr, Object.class))
                .collect(Collectors.toList());
    }

    @NotNull
    @Override
    public String toString() {
        return hostMetadata.toString() + "," + serializeMetrics(metrics);
    }

    public static HostStatus of(@NonNull final HostMetadata hostMetadata,
                                @NonNull final Collection<
                                        MetricValue> metrics
    ) {
        return new HostStatus(hostMetadata, metrics);
    }

    public static HostStatus from(@NonNull final String serialized) {
        val parts = serialized.split(",", 2);
        return new HostStatus(
                new HostMetadata(
                        parts[0].split(":")[0],
                        Integer.parseInt(parts[0].split(":")[1])
                ),
                deserializeMetrics(parts[1])
        );
    }
}
