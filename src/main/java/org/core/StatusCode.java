package org.core;

import lombok.Getter;

import java.util.Map;

@Getter
public enum StatusCode {

    OK(200),

    INTERNAL_SERVER_ERROR(500),
    NOT_IMPLEMENTED(501),
    SERVICE_UNAVAILABLE(503),

    BAD_REQUEST(400),
    NOT_FOUND(404);

    private static final int OK_STATUS_CODE = 200;
    private static final int INTERNAL_SERVER_ERROR_STATUS_CODE = 500;
    private static final int NOT_IMPLEMENTED_STATUS_CODE = 501;
    private static final int SERVICE_UNAVAILABLE_STATUS_CODE = 503;
    private static final int BAD_REQUEST_STATUS_CODE = 400;
    private static final int NOT_FOUND_STATUS_CODE = 404;

    private static final Map<Integer, StatusCode> STATUS_CODE_MAP = Map.of(
            OK_STATUS_CODE, OK,
            INTERNAL_SERVER_ERROR_STATUS_CODE, INTERNAL_SERVER_ERROR,
            NOT_IMPLEMENTED_STATUS_CODE, NOT_IMPLEMENTED,
            SERVICE_UNAVAILABLE_STATUS_CODE, SERVICE_UNAVAILABLE,
            BAD_REQUEST_STATUS_CODE, BAD_REQUEST,
            NOT_FOUND_STATUS_CODE, NOT_FOUND
    );

    private final int statusCode;

    StatusCode(final int code) {
        this.statusCode = code;
    }

    public static StatusCode valueOf(final int code) {
        return STATUS_CODE_MAP.get(code);
    }
}
