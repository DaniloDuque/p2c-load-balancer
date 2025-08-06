package org.http.parser;

import com.sun.net.httpserver.HttpExchange;
import lombok.NonNull;
import org.http.model.request.Request;

public interface InputParser {
    Request parse(@NonNull HttpExchange exchange);
}
