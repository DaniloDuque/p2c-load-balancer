package org.core.filter;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;
import lombok.Builder;
import lombok.NonNull;
import org.core.metric.MetricManager;
import org.core.metric.MetricName;

import java.io.IOException;

@Builder
public final class NumberOfRequestsMetricFilter extends Filter {

    @NonNull
    private final MetricManager metricManager;

    @Builder.Default
    private final MetricName metricName = MetricName.NUMBER_OF_REQUESTS;

    @Override
    public void doFilter(
            @NonNull final HttpExchange exchange,
            @NonNull final Chain chain) throws IOException {
        metricManager
                .getMetric(metricName)
                .ifPresent(
                        metric -> metric.update(1)
                );
        chain.doFilter(exchange);
    }

    @Override
    public String description() {
        return "Tracks number of requests";
    }
}
