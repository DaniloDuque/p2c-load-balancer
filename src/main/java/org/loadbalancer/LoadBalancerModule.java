package org.loadbalancer;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import lombok.NonNull;
import org.core.parser.DefaultInputParser;
import org.core.parser.InputParser;
import org.core.request.DefaultRequestProcessor;
import org.core.resource.DefaultResource;
import org.core.resource.Resource;
import org.loadbalancer.client.DefaultWorkerClient;
import org.loadbalancer.client.WorkerClient;
import org.loadbalancer.registry.CpuUtilizationAndNumberOfRequestsScorer;
import org.loadbalancer.registry.DefaultLBRegistry;
import org.loadbalancer.registry.HostScorer;
import org.loadbalancer.registry.LBRegistry;
import org.loadbalancer.registry.PowerOfTwoChoices;
import org.core.error.DefaultErrorBuilder;
import org.core.error.ErrorBuilder;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Collection;
import java.util.HashMap;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.Filter;
import org.core.Config;
import org.core.handler.GenericHandler;
import org.loadbalancer.core.ProxyResponseBuilder;
import org.loadbalancer.core.UpdateWorkerMetricsResponseBuilder;
import org.core.request.Method;
import org.core.response.ResponseWriter;
import org.core.response.DefaultResponseWriter;
import org.core.response.GetMethodResponseWriter;
import org.core.response.PostMethodResponseWriter;
import org.core.response.HeadMethodResponseWriter;

public final class LoadBalancerModule extends AbstractModule {
    private static final String LOAD_BALANCER_SERVER_NAME
            = "Load-Balancer-Server";

    private final int port;
    private final String errorPath;


    public LoadBalancerModule(final int port,
                              @NonNull final String errorPath) {
        this.port = port;
        this.errorPath = errorPath;
    }

    @Override
    protected void configure() {
        bind(InputParser.class).to(
                DefaultInputParser.class
        );
        bind(WorkerClient.class).to(
                DefaultWorkerClient.class
        );
        bind(HostScorer.class).to(
                CpuUtilizationAndNumberOfRequestsScorer.class
        );
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
        return new DefaultErrorBuilder(
                errorResource,
                LOAD_BALANCER_SERVER_NAME,
                dateFormat
        );
    }

    @Provides
    @Singleton
    LBRegistry provideLBRegistry(@NonNull final HostScorer hostScorer) {
        return DefaultLBRegistry.builder()
                .registryMap(new ConcurrentHashMap<>())
                .selector(new PowerOfTwoChoices(hostScorer))
                .build();
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
                         @NonNull final LBRegistry registry,
                         @NonNull final WorkerClient workerClient,
                         @NonNull final ResponseWriter responseWriter) {
        return new Config() {
            @Override
            public Map<String, HttpHandler> getServerHandlers() {
                ProxyResponseBuilder proxyBuilder =
                        ProxyResponseBuilder.builder()
                        .client(workerClient)
                        .errorBuilder(errorBuilder)
                        .registry(registry)
                        .build();

                return Map.of(
                        "/solver/queen", GenericHandler.builder()
                                .parser(inputParser)
                                .requestProcessor(
                                        DefaultRequestProcessor.builder()
                                        .responseBuilders(Map.of(
                                                Method.GET, proxyBuilder,
                                                Method.POST, proxyBuilder,
                                                Method.PUT, proxyBuilder,
                                                Method.DELETE, proxyBuilder,
                                                Method.PATCH, proxyBuilder,
                                                Method.HEAD, proxyBuilder,
                                                Method.OPTIONS, proxyBuilder))
                                        .errorBuilder(errorBuilder)
                                        .build())
                                .responseProcessor(responseWriter)
                                .build(),
                        "/metrics", GenericHandler.builder()
                                .parser(inputParser)
                                .requestProcessor(
                                        DefaultRequestProcessor.builder()
                                        .responseBuilders(Map.of(Method.POST,
                                            UpdateWorkerMetricsResponseBuilder
                                                        .builder()
                                                        .registry(registry)
                                                        .build()))
                                        .errorBuilder(errorBuilder)
                                        .build())
                                .responseProcessor(responseWriter)
                                .build());
            }

            @Override
            public Map<String, Collection<Filter>> getServerFilters() {
                return new HashMap<>();
            }
        };
    }
}
