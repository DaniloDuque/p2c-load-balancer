package org.core.response;

import com.google.inject.Singleton;
import com.sun.net.httpserver.HttpExchange;
import lombok.Builder;
import lombok.NonNull;
import org.core.request.Method;

import java.io.IOException;
import java.util.Map;

@Builder
@Singleton
public final class DefaultResponseWriter implements ResponseWriter {
    private final Map<Method, MethodResponseWriter> responseWriters;
    private final MethodResponseWriter defaultResponseWriter;

    @Override
    public void write(@NonNull final Response response,
                      @NonNull final Method method,
                      @NonNull final HttpExchange exchange) throws IOException {
        responseWriters
                .getOrDefault(method, defaultResponseWriter)
                .write(response, exchange);
    }
}
