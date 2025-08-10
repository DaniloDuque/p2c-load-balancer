package org.loadbalancer;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpHandler;
import lombok.Builder;
import lombok.val;
import org.core.Config;
import org.core.handler.GenericHandler;
import org.core.parser.DefaultInputParser;
import org.core.parser.InputParser;
import org.core.processor.DefaultRequestProcessor;
import org.core.processor.RequestProcessor;
import org.core.resource.Resource;
import org.loadbalancer.client.DefaultWorkerClient;
import org.loadbalancer.core.ProxyResponseBuilder;
import org.loadbalancer.core.UpdateWorkerMetricsResponseBuilder;
import org.loadbalancer.registry.DefaultLBRegistry;
import org.loadbalancer.registry.HostScorer;
import org.loadbalancer.registry.LBRegistry;
import org.model.error.DefaultErrorBuilder;
import org.loadbalancer.registry.PowerOfTwoChoices;
import org.loadbalancer.registry.CpuUtilizationAndNumberOfRequestsScorer;
import org.model.error.ErrorBuilder;
import org.model.request.Method;
import org.model.response.ResponseBuilder;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

@Builder
public final class LoadBalancerConfig implements Config {
    private static final String METRICS_REGISTRY_PATH = "/metrics";
    private static final String LOAD_BALANCING_PATH = "/solver/queen";
    private static final String SERVER_NAME = "Load-Balancer-Server";
    private static final String DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss 'GMT'";
    private static final Locale LOCALE_FORMAT = Locale.ENGLISH;
    private static final String TIME_ZONE = "GMT";
    private static final String LB_HOST = "localhost";
    private static final Integer LB_PORT = 8080;

    private final Resource errorsDirectory;
    private RequestProcessor lbRequestProcessor;
    private Map<Method, ResponseBuilder> lbResponseBuilders;
    private RequestProcessor registryRequestProcessor;
    private Map<Method, ResponseBuilder> registryResponseBuilders;
    private InputParser inputParser;
    private ErrorBuilder errorBuilder;
    private DefaultLBRegistry workerMetricRegistry;
    private HostScorer hostScorer;

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
        handlers.put(LOAD_BALANCING_PATH,
                GenericHandler.builder()
                        .parser(inputParser())
                        .requestProcessor(lbRequestProcessor())
                        .build());
        handlers.put(METRICS_REGISTRY_PATH,
                GenericHandler.builder()
                        .parser(inputParser())
                        .requestProcessor(registryRequestProcessor())
                        .build());
        return handlers;
    }

    @Override
    public Map<String, Collection<Filter>> getServerFilters() {
        return new HashMap<>();
    }

    private InputParser inputParser() {
        if (inputParser == null) {
            inputParser = new DefaultInputParser();
        }
        return inputParser;
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

    private LBRegistry workerMetricRegistry() {
        if (workerMetricRegistry == null) {
            workerMetricRegistry = DefaultLBRegistry.builder()
                    .registryMap(new ConcurrentHashMap<>())
                    .selector(new PowerOfTwoChoices(hostScorer()))
                    .build();
        }
        return workerMetricRegistry;
    }

    private HostScorer hostScorer() {
        if (hostScorer == null) {
            hostScorer = new CpuUtilizationAndNumberOfRequestsScorer();
        }
        return hostScorer;
    }

    private RequestProcessor registryRequestProcessor() {
        if (registryRequestProcessor == null) {
            registryRequestProcessor = DefaultRequestProcessor.builder()
                    .responseBuilders(registryResponseBuilders())
                    .errorBuilder(errorBuilder())
                    .build();
        }
        return registryRequestProcessor;
    }

    private Map<Method, ResponseBuilder> registryResponseBuilders() {
        if (registryResponseBuilders == null) {
            registryResponseBuilders = Map.of(
                    Method.POST, UpdateWorkerMetricsResponseBuilder
                            .builder()
                            .registry(workerMetricRegistry())
                            .build()
            );
        }
        return registryResponseBuilders;
    }

    private RequestProcessor lbRequestProcessor() {
        if (lbRequestProcessor == null) {
            lbRequestProcessor = DefaultRequestProcessor.builder()
                    .responseBuilders(lbResponseBuilders())
                    .errorBuilder(errorBuilder())
                    .build();
        }
        return lbRequestProcessor;
    }

    private Map<Method, ResponseBuilder> lbResponseBuilders() {
        if (lbResponseBuilders == null) {
            val proxyBuilder = ProxyResponseBuilder
                    .builder()
                    .client(new DefaultWorkerClient())
                    .errorBuilder(errorBuilder())
                    .registry(workerMetricRegistry())
                    .build();
            lbResponseBuilders = Map.of(
                    Method.GET, proxyBuilder,
                    Method.POST, proxyBuilder,
                    Method.PUT, proxyBuilder,
                    Method.DELETE, proxyBuilder,
                    Method.PATCH, proxyBuilder,
                    Method.HEAD, proxyBuilder,
                    Method.OPTIONS, proxyBuilder
            );
        }
        return lbResponseBuilders;
    }
}
