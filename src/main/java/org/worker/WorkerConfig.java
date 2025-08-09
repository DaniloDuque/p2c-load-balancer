package org.worker;

import com.sun.net.httpserver.HttpHandler;
import lombok.Builder;
import lombok.NonNull;
import org.core.Config;
import org.core.client.MetricClient;
import org.core.client.DefaultMetricClient;
import org.core.client.HostMetadata;
import org.core.handler.ActiveRequestMetricHandler;
import org.core.metric.ActiveRequestsMetric;
import org.core.metric.Metric;
import org.core.metric.MetricManager;
import org.core.metric.MetricName;
import org.core.model.error.DefaultErrorBuilder;
import org.core.model.error.ErrorBuilder;
import org.core.model.request.Method;
import org.core.model.resource.Resource;
import org.core.model.response.ResponseBuilder;
import org.core.parser.DefaultInputParser;
import org.core.parser.InputParser;
import org.core.processor.DefaultRequestProcessor;
import org.core.processor.RequestProcessor;
import org.worker.core.NQueenResponse;
import org.worker.core.Solver;
import org.worker.core.genetic.GeneticNQueenSolver;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
import java.util.TimeZone;
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
    private static final String LB_HOST = "http://localhost";
    private static final Integer LB_PORT = 8080;
    private static final Integer METRIC_MANAGER_INITIAL_DELAY = 5;
    private static final Integer METRIC_MANAGER_DELAY = 2;
    private static final TimeUnit METRIC_MANAGER_TIME_UNIT = TimeUnit.SECONDS;
    private static final ScheduledExecutorService METRIC_EXECUTOR_SERVICE =
            Executors.newScheduledThreadPool(1);

    private final Resource errorsDirectory;
    private RequestProcessor requestProcessor;
    private Map<Method, ResponseBuilder> responseBuilders;
    private InputParser inputParser;
    private Solver problemSolver;
    private ErrorBuilder errorBuilder;
    private MetricManager metricManager;
    private MetricClient metricClient;
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

    public Map<String, HttpHandler> getServerHandlers() {
        Map<String, HttpHandler> handlers = new HashMap<>();
        handlers.put(NQUEEN_SOLVER_PATH,
                ActiveRequestMetricHandler.builder()
                        .parser(inputParser())
                        .requestProcessor(requestProcessor())
                        .metricManager(metricManager())
                        .build());
        return handlers;
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
        metrics().forEach(metricManager::register);
    }

    private Map<MetricName, Metric<?>> metrics() {
        if (metrics == null) {
            metrics = Map.of(
                    MetricName.ACTIVE_REQUESTS, new ActiveRequestsMetric()
            );
        }
        return metrics;
    }

    private MetricClient metricClient() {
        if (metricClient == null) {
            metricClient = DefaultMetricClient.builder()
                    .hostMetadata(lbHostMetadata())
                    .build();
        }
        return metricClient;
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
