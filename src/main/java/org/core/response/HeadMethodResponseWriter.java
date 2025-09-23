package org.core.response;

import com.google.inject.Singleton;
import com.sun.net.httpserver.HttpExchange;
import lombok.NonNull;

import java.io.IOException;

@Singleton
public final class HeadMethodResponseWriter implements MethodResponseWriter {
    @Override
    public void write(@NonNull final Response response,
                      @NonNull final HttpExchange exchange) throws IOException {
        response.headers().forEach((key, value) ->
                exchange.getResponseHeaders().set(key, value));

        long contentLength = response.body() != null
                ? response.body().available()
                : 0;

        exchange.sendResponseHeaders(
                response.statusCode().getStatusCode(),
                contentLength
        );

        exchange.getResponseBody().close();
    }
}
