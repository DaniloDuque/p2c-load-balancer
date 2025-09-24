package org.core.filter;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.UUID;

@NoArgsConstructor
public final class RequestIdLogFilter extends Filter {

    @Override
    public void doFilter(
            @NonNull final HttpExchange exchange,
            @NonNull final Chain chain) throws IOException {
        var requestId = UUID.randomUUID().toString();
        try (MDC.MDCCloseable ignored
                     = MDC.putCloseable("requestId", requestId)) {
            chain.doFilter(exchange);
        }
    }

    @Override
    public String description() {
        return "Creates and attaches a unique "
                + "request id to all the logs for this request";
    }
}
