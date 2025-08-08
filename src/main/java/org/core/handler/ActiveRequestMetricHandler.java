package org.core.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.core.metric.MetricManager;
import org.core.metric.MetricName;
import org.core.metric.UpdatableMetric;
import org.core.model.request.Method;
import org.core.model.response.Response;
import org.core.parser.InputParser;
import org.core.processor.RequestProcessor;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;

@Log4j2
@AllArgsConstructor
public final class ActiveRequestMetricHandler implements HttpHandler {
    @NonNull
    private final InputParser parser;

    @NonNull
    private final MetricManager metricManager;

    @NonNull
    private final RequestProcessor requestProcessor;

    @Override
    public void handle(
            @NonNull final HttpExchange exchange) throws IOException {
        log.info("Received request: {}", exchange.getRequestMethod());
        Optional<UpdatableMetric<Integer>> throughputMetric =
                metricManager.getMetric(MetricName.ACTIVE_REQUESTS);
        throughputMetric.ifPresent(metric -> metric.update(1));
        val parsedRequest = parser.parse(exchange);
        val response = requestProcessor.process(parsedRequest);
        sendResponse(exchange, response);
        throughputMetric.ifPresent(metric -> metric.update(-1));
    }

    private void sendResponse(
            @NonNull final HttpExchange exchange,
            @NonNull final Response response) throws IOException {

        response.headers().forEach((key, value) ->
                exchange.getResponseHeaders().set(key, value));

        byte[] responseBody = response.body() != null
                ? response.body().readAllBytes()
                : new byte[0];
        boolean needsResponseBody = Method.needsResponseBody(
                Method.from(exchange.getRequestMethod())
        );

        exchange.sendResponseHeaders(
                response.statusCode().getStatusCode(),
                needsResponseBody ? responseBody.length : -1
        );

        if (needsResponseBody) {
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBody);
                os.flush();
            }
        }
    }
}
