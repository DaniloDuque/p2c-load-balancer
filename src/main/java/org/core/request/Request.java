package org.core.request;

import lombok.AllArgsConstructor;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public final class Request {
    private final Method method;
    private final String path;
    private final String httpVersion;
    private final Map<String, String> headers;
    private final InputStream body;

    public Method method() {
        return method;
    }

    public String path() {
        return path;
    }

    public String httpVersion() {
        return httpVersion;
    }

    public Map<String, String> headers() {
        return headers;
    }

    public InputStream body() {
        return body;
    }

    public Map<String, String> queryParams() {
        Map<String, String> params = new HashMap<>();
        int queryIndex = path.indexOf('?');
        if (queryIndex != -1 && queryIndex < path.length() - 1) {
            String queryString = path.substring(queryIndex + 1);
            for (String param : queryString.split("&")) {
                String[] parts = param.split("=", 2);
                if (parts.length == 2) {
                    params.put(parts[0], parts[1]);
                }
            }
        }
        return params;
    }
}
