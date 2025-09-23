package org.core.response;

import com.sun.net.httpserver.HttpExchange;
import org.core.request.Method;

import java.io.IOException;

public interface ResponseWriter {
    void write(
            Response response,
            Method method,
            HttpExchange exchange) throws IOException;
}
