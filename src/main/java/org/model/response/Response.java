package org.model.response;

import org.core.StatusCode;

import java.io.InputStream;
import java.util.Map;

public record Response(StatusCode statusCode,
                       Map<String, String> headers,
                       InputStream body) {
}
