package org.core.response;

import com.google.inject.Singleton;
import com.sun.net.httpserver.HttpExchange;
import lombok.NonNull;

import java.io.IOException;
import java.io.OutputStream;

@Singleton
public final class GetMethodResponseWriter implements MethodResponseWriter {
    @Override
    public void write(@NonNull final Response response,
                      @NonNull final HttpExchange exchange) throws IOException {
        response.headers().forEach((key, value) ->
                exchange.getResponseHeaders().set(key, value));

        byte[] responseBody = response.body() != null
                ? response.body().readAllBytes()
                : new byte[0];

        exchange.sendResponseHeaders(
                response.statusCode().getStatusCode(),
                responseBody.length
        );

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBody);
            os.flush();
        }
    }
}
