package org.http.model.response;

import org.http.StatusCode;

import java.io.InputStream;
import java.util.Map;

public record Response(StatusCode statusCode,
                       Map<String, String> headers,
                       InputStream body) {
}
