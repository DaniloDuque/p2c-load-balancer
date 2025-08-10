package org.core.metric;

import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

public record MetricValue(MetricName name, Object value) {
    public <T> MetricValue(@NonNull final String metricValue,
                           @NonNull final Class<T> valueType) {
        this(parseName(metricValue), parseValue(metricValue, valueType));
    }

    private static MetricName parseName(@NonNull final String metricValue) {
        return MetricName.valueOf(
                metricValue.substring(
                        1,
                        metricValue.length() - 1
                ).split(",", 2)[0]
        );
    }

    private static <T> T parseValue(@NonNull final String metricValue,
                                    @NonNull final Class<T> valueType) {
        return valueType.cast(
                metricValue.substring(
                        1,
                        metricValue.length() - 1
                ).split(",", 2)[1]);
    }

    @NotNull
    @Override
    public String toString() {
        return String.format("[%s,%s]", name, value);
    }
}
