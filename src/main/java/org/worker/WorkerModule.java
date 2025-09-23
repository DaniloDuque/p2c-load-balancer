package org.worker;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import lombok.NonNull;
import org.core.HostMetadata;
import org.core.metric.CpuUtilizationMetric;
import org.core.metric.MetricManager;
import org.core.metric.MetricName;
import org.core.metric.NumberOfRequestsMetric;
import org.core.parser.DefaultInputParser;
import org.core.parser.InputParser;
import org.core.error.DefaultErrorBuilder;
import org.core.error.ErrorBuilder;
import org.core.resource.DefaultResource;
import org.core.resource.Resource;
import org.worker.client.DefaultMetricClient;
import org.worker.client.DefaultMetricRequestAdapter;
import org.worker.client.MetricClient;
import org.worker.core.Solver;
import org.worker.core.genetic.GeneticNQueenSolver;

import java.net.http.HttpClient;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.Map;
import java.util.Collection;
import java.util.Collections;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.Filter;
import org.core.Config;
import org.core.handler.GenericHandler;
import org.core.request.DefaultRequestProcessor;
import org.core.filter.NumberOfRequestsMetricFilter;
import org.worker.core.NQueenResponse;
import org.core.request.Method;
import com.google.inject.name.Named;
import org.core.response.ResponseWriter;
import org.core.response.DefaultResponseWriter;
import org.core.response.GetMethodResponseWriter;
import org.core.response.PostMethodResponseWriter;
import org.core.response.HeadMethodResponseWriter;

public final class WorkerModule extends AbstractModule {
    private static final String SERVER_NAME = "Worker-Server";
    private static final String HOST_NAME = "localhost";
    private static final String LOAD_BALANCER_HOST_NAME = "localhost";
    private static final int LOAD_BALANCER_PORT = 8080;
    private static final int DEFAULT_DELAY_IN_SECONDS = 5;
    private static final int DEFAULT_INITIAL_DELAY_IN_SECONDS = 1;

    private final int port;
    private final String errorPath;

    public WorkerModule(final int port, @NonNull final String errorPath) {
        this.port = port;
        this.errorPath = errorPath;
    }

    @Override
    protected void configure() {
        bind(InputParser.class).to(DefaultInputParser.class);
        bind(Solver.class).to(GeneticNQueenSolver.class);
    }

    @Provides
    @Singleton
    Resource provideErrorResource(@NonNull final String errorPath) {
        return new DefaultResource(errorPath);
    }

    @Provides
    @Singleton
    SimpleDateFormat provideDateFormat() {
        SimpleDateFormat format = new SimpleDateFormat(
                "EEE, dd MMM yyyy HH:mm:ss 'GMT'",
                Locale.ENGLISH
        );
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        return format;
    }

    @Provides
    @Singleton
    ErrorBuilder provideErrorBuilder(@NonNull final SimpleDateFormat dateFormat,
                                     @NonNull final Resource errorResource) {
        return new DefaultErrorBuilder(errorResource, SERVER_NAME, dateFormat);
    }

    @Provides
    @Singleton
    @Named("worker")
    HostMetadata provideHostMetadata() {
        return new HostMetadata(HOST_NAME, port);
    }

    @Provides
    @Singleton
    @Named("loadbalancer")
    HostMetadata provideLBHostMetadata() {
        return new HostMetadata(LOAD_BALANCER_HOST_NAME, LOAD_BALANCER_PORT);
    }

    @Provides
    @Singleton
    MetricClient provideMetricClient(
            @Named("worker") @NonNull final HostMetadata hostMetadata,
            @Named("loadbalancer") @NonNull final HostMetadata lbHostMetadata) {
        return DefaultMetricClient.builder()
                .adapter(new DefaultMetricRequestAdapter())
                .client(HttpClient.newHttpClient())
                .lbHostMetadata(lbHostMetadata)
                .hostMetadata(hostMetadata)
                .build();
    }

    @Provides
    @Singleton
    MetricManager provideMetricManager(
            @NonNull final MetricClient metricClient) {
        MetricManager manager = MetricManager.builder()
                .client(metricClient)
                .timeUnit(TimeUnit.SECONDS)
                .initialDelay(DEFAULT_INITIAL_DELAY_IN_SECONDS)
                .delay(DEFAULT_DELAY_IN_SECONDS)
                .scheduler(Executors.newScheduledThreadPool(1))
                .build();

        manager.register(
                MetricName.NUMBER_OF_REQUESTS,
                new NumberOfRequestsMetric()
        );
        manager.register(
                MetricName.CPU_UTILIZATION,
                new CpuUtilizationMetric()
        );
        return manager;
    }

    @Provides
    @Singleton
    ResponseWriter provideResponseWriter() {
        return DefaultResponseWriter.builder()
                .responseWriters(Map.of(
                        Method.GET, new GetMethodResponseWriter(),
                        Method.POST, new PostMethodResponseWriter(),
                        Method.HEAD, new HeadMethodResponseWriter()))
                .defaultResponseWriter(new HeadMethodResponseWriter())
                .build();
    }

    @Provides
    @Singleton
    Config provideConfig(@NonNull final InputParser inputParser,
                         @NonNull final ErrorBuilder errorBuilder,
                         @NonNull final Solver solver,
                         @NonNull final SimpleDateFormat dateFormat,
                         @NonNull final MetricManager metricManager,
                         @NonNull final ResponseWriter responseWriter) {
        return new Config() {
            @Override
            public Map<String, HttpHandler> getServerHandlers() {
                return Map.of("/solver/queen", GenericHandler.builder()
                        .parser(inputParser)
                        .requestProcessor(DefaultRequestProcessor.builder()
                                .responseBuilders(
                                        Map.of(
                                                Method.GET,
                                                NQueenResponse.builder()
                                                    .errorBuilder(errorBuilder)
                                                        .serverName(SERVER_NAME)
                                                        .dateFormat(dateFormat)
                                                        .solver(solver)
                                                        .build()))
                                .errorBuilder(errorBuilder)
                                .build())
                        .responseProcessor(responseWriter)
                        .build());
            }

            @Override
            public Map<String, Collection<Filter>> getServerFilters() {
                return Map.of("/solver/queen", Collections.singletonList(
                        NumberOfRequestsMetricFilter.builder()
                                .metricManager(metricManager)
                                .build()));
            }
        };
    }
}
