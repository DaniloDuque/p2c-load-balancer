package org.worker;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpHandler;
import lombok.Builder;
import lombok.NonNull;
import org.core.Config;
import org.core.handler.GenericHandler;
import org.core.metric.MetricManager;
import org.core.metric.MetricName;
import org.core.metric.UpdatableMetric;
import org.core.metric.CpuUtilizationMetric;
import org.core.metric.NumberOfRequestsMetric;
import org.core.metric.Metric;
import org.worker.client.DefaultMetricRequestAdapter;
import org.worker.client.MetricClient;
import org.worker.client.DefaultMetricClient;
import org.core.HostMetadata;
import org.core.filter.NumberOfRequestsMetricFilter;
import org.model.error.DefaultErrorBuilder;
import org.model.error.ErrorBuilder;
import org.model.request.Method;
import org.core.resource.Resource;
import org.model.response.ResponseBuilder;
import org.core.parser.DefaultInputParser;
import org.core.parser.InputParser;
import org.core.processor.DefaultRequestProcessor;
import org.core.processor.RequestProcessor;
import org.worker.core.NQueenResponse;
import org.worker.core.Solver;
import org.worker.core.genetic.GeneticNQueenSolver;

import java.net.http.HttpClient;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Builder
public final class WorkerConfig implements Config {
    private static final String NQUEEN_SOLVER_PATH = "/solver/queen";
    private static final String SERVER_NAME = "Worker-Server";
    private static final String DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss 'GMT'";
    private static final Locale LOCALE_FORMAT = Locale.ENGLISH;
    private static final String TIME_ZONE = "GMT";
    private static final Integer LB_PORT = 8080;
    private static final String LB_HOST = "localhost";
    private static final String HOST = "localhost";
    private static final Integer METRIC_MANAGER_INITIAL_DELAY = 1;
    private static final Integer METRIC_MANAGER_DELAY = 5;
    private static final TimeUnit METRIC_MANAGER_TIME_UNIT = TimeUnit.SECONDS;
    private static final ScheduledExecutorService METRIC_EXECUTOR_SERVICE =
            Executors.newScheduledThreadPool(1);

    private final int port;
    private final Resource errorsDirectory;
    private RequestProcessor requestProcessor;
    private Map<Method, ResponseBuilder> responseBuilders;
    private InputParser inputParser;
    private Solver problemSolver;
    private ErrorBuilder errorBuilder;
    private MetricManager metricManager;
    private MetricClient metricClient;
    private HostMetadata hostMetadata;
    private HostMetadata lbHostMetadata;
    private Map<MetricName, Metric<?>> metrics;

    private static SimpleDateFormat simpleDateFormat() {
        final SimpleDateFormat httpDateFormat = new SimpleDateFormat(
                DATE_FORMAT,
                LOCALE_FORMAT
        );
        httpDateFormat.setTimeZone(TimeZone.getTimeZone(TIME_ZONE));
        return httpDateFormat;
    }

    @Override
    public Map<String, HttpHandler> getServerHandlers() {
        Map<String, HttpHandler> handlers = new HashMap<>();
        handlers.put(NQUEEN_SOLVER_PATH,
                GenericHandler.builder()
                        .parser(inputParser())
                        .requestProcessor(requestProcessor())
                        .build());
        return handlers;
    }

    @Override
    public Map<String, Collection<Filter>> getServerFilters() {
        Map<String, Collection<Filter>> filters = new HashMap<>();
        NumberOfRequestsMetricFilter activeRequestFilter
                = NumberOfRequestsMetricFilter.builder()
                .metricManager(metricManager())
                .build();
        filters.put(
                NQUEEN_SOLVER_PATH,
                Collections.singletonList(activeRequestFilter)
        );
        return filters;
    }

    private MetricManager metricManager() {
        if (metricManager == null) {
            metricManager = MetricManager.builder()
                    .client(metricClient())
                    .timeUnit(METRIC_MANAGER_TIME_UNIT)
                    .initialDelay(METRIC_MANAGER_INITIAL_DELAY)
                    .delay(METRIC_MANAGER_DELAY)
                    .scheduler(METRIC_EXECUTOR_SERVICE)
                    .build();
            registerMetrics(metricManager);
        }
        return metricManager;
    }

    private void registerMetrics(@NonNull final MetricManager metricManager) {
        metrics().forEach((name, metric) -> {
            if (metric instanceof UpdatableMetric<?>) {
                metricManager.register(name, (UpdatableMetric<?>) metric);
            } else {
                metricManager.register(name, metric);
            }
        });
    }

    private Map<MetricName, Metric<?>> metrics() {
        if (metrics == null) {
            metrics = Map.of(
                    MetricName.NUMBER_OF_REQUESTS, new NumberOfRequestsMetric(),
                    MetricName.CPU_UTILIZATION, new CpuUtilizationMetric()
            );
        }
        return metrics;
    }

    private MetricClient metricClient() {
        if (metricClient == null) {
            metricClient = DefaultMetricClient.builder()
                    .adapter(new DefaultMetricRequestAdapter())
                    .client(HttpClient.newHttpClient())
                    .lbHostMetadata(lbHostMetadata())
                    .hostMetadata(hostMetadata())
                    .build();
        }
        return metricClient;
    }

    private HostMetadata hostMetadata() {
        if (hostMetadata == null) {
            hostMetadata = new HostMetadata(HOST, port);
        }
        return hostMetadata;
    }

    private HostMetadata lbHostMetadata() {
        if (lbHostMetadata == null) {
            lbHostMetadata = new HostMetadata(LB_HOST, LB_PORT);
        }
        return lbHostMetadata;
    }

    private RequestProcessor requestProcessor() {
        if (requestProcessor == null) {
            requestProcessor = DefaultRequestProcessor.builder()
                    .responseBuilders(responseBuilders())
                    .errorBuilder(errorBuilder())
                    .build();
        }
        return requestProcessor;
    }

    private InputParser inputParser() {
        if (inputParser == null) {
            inputParser = new DefaultInputParser();
        }
        return inputParser;
    }

    private Solver problemSolver() {
        if (problemSolver == null) {
            problemSolver = new GeneticNQueenSolver();
        }
        return problemSolver;
    }

    private ErrorBuilder errorBuilder() {
        if (errorBuilder == null) {
            errorBuilder = new DefaultErrorBuilder(
                    errorsDirectory,
                    SERVER_NAME,
                    simpleDateFormat()
            );
        }
        return errorBuilder;
    }

    private String serverName() {
        return SERVER_NAME;
    }

    private Map<Method, ResponseBuilder> responseBuilders() {
        if (responseBuilders == null) {
            responseBuilders = Map.of(
                    Method.GET, NQueenResponse
                            .builder()
                            .errorBuilder(errorBuilder())
                            .serverName(serverName())
                            .dateFormat(simpleDateFormat())
                            .solver(problemSolver())
                            .build()
            );
        }
        return responseBuilders;
    }
}
