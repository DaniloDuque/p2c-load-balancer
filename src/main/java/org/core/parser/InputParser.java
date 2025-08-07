package org.core.parser;

import com.sun.net.httpserver.HttpExchange;
import lombok.NonNull;
import org.core.model.request.Request;

public interface InputParser {
    Request parse(@NonNull HttpExchange exchange);
}
