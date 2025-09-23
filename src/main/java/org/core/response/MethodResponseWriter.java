package org.core.response;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public interface MethodResponseWriter {
    void write(Response response, HttpExchange exchange) throws IOException;
}
