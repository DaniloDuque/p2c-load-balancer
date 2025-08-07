package org.core.model.request;

import lombok.NonNull;

public enum Method {
    GET, POST, PUT, DELETE, OPTIONS, HEAD, PATCH, TRACE, CONNECT, ERROR;

    public static Method from(@NonNull final String method) {
        try {
            return Method.valueOf(method.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ERROR;
        }
    }

    public static boolean needsResponseBody(@NonNull final Method method) {
        return method != HEAD;
    }
}
