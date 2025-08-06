package org.http.parser;

import com.sun.net.httpserver.HttpExchange;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.http.model.request.Method;
import org.http.model.request.Request;

import java.util.HashMap;
import java.util.Map;

@Log4j2
public final class DefaultInputParser implements InputParser {

    @Override
    public Request parse(@NonNull final HttpExchange exchange) {
        val method = Method.from(exchange.getRequestMethod());
        val uri = exchange.getRequestURI().toString();
        val httpVersion = exchange.getProtocol();
        val requestHeaders = exchange.getRequestHeaders();
        val inputStream = exchange.getRequestBody();

        Map<String, String> headers = new HashMap<>();
        requestHeaders.forEach((name, values) -> {
            if (!values.isEmpty()) {
                headers.put(name, String.join(", ", values));
            }
        });

        return new Request(
                method,
                uri,
                httpVersion,
                headers,
                inputStream
        );
    }
}
