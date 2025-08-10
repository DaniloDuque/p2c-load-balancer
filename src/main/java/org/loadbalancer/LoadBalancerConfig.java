package org.loadbalancer;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpHandler;
import lombok.Builder;
import org.core.Config;
import org.core.parser.DefaultInputParser;
import org.core.parser.InputParser;
import org.core.processor.DefaultRequestProcessor;
import org.core.processor.RequestProcessor;
import org.core.resource.Resource;
import org.model.error.DefaultErrorBuilder;
import org.model.error.ErrorBuilder;
import org.model.request.Method;
import org.model.response.ResponseBuilder;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;

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
    private RequestProcessor requestProcessor;
    private Map<Method, ResponseBuilder> responseBuilders;
    private InputParser inputParser;
    private ErrorBuilder errorBuilder;

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
        return handlers;
    }

    @Override
    public Map<String, Collection<Filter>> getServerFilters() {
        return new HashMap<>();
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

            );
        }
        return responseBuilders;
    }
}
