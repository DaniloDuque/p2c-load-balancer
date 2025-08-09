package org.core.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.model.request.Method;
import org.model.response.Response;
import org.core.parser.InputParser;
import org.core.processor.RequestProcessor;

import java.io.IOException;
import java.io.OutputStream;

@Log4j2
@AllArgsConstructor
public final class GenericHandler implements HttpHandler {
    @NonNull
    private final InputParser parser;

    @NonNull
    private final RequestProcessor requestProcessor;

    @Override
    public void handle(
            @NonNull final HttpExchange exchange) throws IOException {
        log.info("Received request: {}", exchange.getRequestMethod());
        val parsedRequest = parser.parse(exchange);
        val response = requestProcessor.process(parsedRequest);
        sendResponse(exchange, response);
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
