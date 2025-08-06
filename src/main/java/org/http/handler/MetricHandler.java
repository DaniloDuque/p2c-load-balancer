package org.http.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.NonNull;

import java.io.IOException;

public class MetricHandler implements HttpHandler {
    @Override
    public void handle(
            @NonNull final HttpExchange exchange) throws IOException {

    }
}
