package org.model.response;

import lombok.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public final class ResponseUtils {
    public static final String DATE = "Date";
    public static final String SERVER = "Server";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String CONNECTION = "Connection";
    public static final String CONNECTION_STATUS = "keep-alive";

    private ResponseUtils() {
    }

    public static Map<String, String> createCommonHeaders(
            @NonNull final String serverName,
            @NonNull final SimpleDateFormat dateFormat,
            @NonNull final String contentType,
            final int contentLength) {

        Map<String, String> headers = new HashMap<>();
        headers.put(SERVER, serverName);
        headers.put(DATE, dateFormat.format(new Date()));
        headers.put(CONTENT_TYPE, contentType);
        headers.put(CONTENT_LENGTH, String.valueOf(contentLength));
        headers.put(CONNECTION, CONNECTION_STATUS);
        return headers;
    }
}
