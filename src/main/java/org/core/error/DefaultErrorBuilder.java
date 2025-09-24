package org.core.error;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.core.StatusCode;
import org.core.request.Request;
import org.core.resource.DefaultResource;
import org.core.resource.Resource;
import org.core.response.Response;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public final class DefaultErrorBuilder implements ErrorBuilder {
    private static final String EXTENSION = ".html";
    private static final String CONNECTION_FOR_ERRORS = "close";
    private static final String MIME_TYPE_FOR_ERRORS = "text/html";
    private static final String DATE = "Date";
    private static final String SERVER = "Server";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String CONNECTION = "Connection";

    private final Map<StatusCode, Resource> errorResources;
    private final String serverName;
    private final SimpleDateFormat dateFormat;

    public DefaultErrorBuilder(
            @NonNull final Resource errorResourceFolder,
            @NonNull final String serverName,
            @NonNull final SimpleDateFormat simpleDateFormat) {
        this.serverName = serverName;
        this.dateFormat = simpleDateFormat;
        this.errorResources = new HashMap<>();
        for (StatusCode statusCode : StatusCode.values()) {
            final Resource errorResource = new DefaultResource(
                    errorResourceFolder, statusCode
                    .name() + EXTENSION
            );
            errorResources.put(statusCode, errorResource);
        }
    }

    @Override
    public Response from(@NonNull final Request request,
                         @NonNull final StatusCode status) {
        val errorResource = errorResources.get(status);
        InputStream body;
        long resourceLength;

        try {
            body = errorResource.openStream();
            resourceLength = errorResource.length();
        } catch (Throwable e) {
            log.warn("Error opening stream: {}", e.getMessage());
            body = null;
            resourceLength = 0;
        }

        val currentDate = new Date();
        val formattedCurrentDate = dateFormat.format(currentDate);
        final Map<String, String> headers = Map.of(
                DATE, formattedCurrentDate,
                SERVER, serverName,
                CONTENT_TYPE, MIME_TYPE_FOR_ERRORS,
                CONTENT_LENGTH, String.valueOf(resourceLength),
                CONNECTION, CONNECTION_FOR_ERRORS
        );

        return new Response(
                status,
                headers,
                body
        );
    }
}
