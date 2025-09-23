package org.core.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.core.parser.InputParser;
import org.core.request.RequestProcessor;
import org.core.response.ResponseWriter;

import java.io.IOException;

@Log4j2
@Builder
public final class GenericHandler implements HttpHandler {
    @NonNull
    private final InputParser parser;

    @NonNull
    private final RequestProcessor requestProcessor;

    @NonNull
    private final ResponseWriter responseProcessor;

    @Override
    public void handle(
            @NonNull final HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String clientAddress = exchange
                .getRemoteAddress()
                .getAddress()
                .getHostAddress();

        log.info("{} {} from {}", method, path, clientAddress);

        val parsedRequest = parser.parse(exchange);
        val response = requestProcessor.process(parsedRequest);
        responseProcessor.write(
                response,
                parsedRequest.method(),
                exchange
        );
    }
}
