package org.http;

import lombok.Getter;

@Getter
public enum StatusCode {
    OK(200),

    NOT_IMPLEMENTED(501),
    INTERNAL_SERVER_ERROR(500),

    BAD_REQUEST(400),
    NOT_FOUND(404);

    private final int statusCode;

    StatusCode(final int code) {
        this.statusCode = code;
    }

}
