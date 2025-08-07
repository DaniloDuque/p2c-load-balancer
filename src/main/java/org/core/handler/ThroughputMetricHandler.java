package org.core.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.NonNull;

import java.io.IOException;

public class ThroughputMetricHandler implements HttpHandler {
    @Override
    public void handle(
            @NonNull final HttpExchange exchange) throws IOException {

    }
}
