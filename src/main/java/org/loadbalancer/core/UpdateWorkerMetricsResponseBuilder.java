package org.loadbalancer.core;

import lombok.Builder;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.core.StatusCode;
import org.core.metric.HostStatus;
import org.loadbalancer.registry.LBRegistry;
import org.model.error.ErrorBuilder;
import org.model.request.Request;
import org.model.response.Response;
import org.model.response.ResponseBuilder;

import java.io.InputStream;
import java.util.Map;

@Log4j2
@Builder
public final class UpdateWorkerMetricsResponseBuilder
        implements ResponseBuilder {
    private static final String CONTENT_TYPE_NAME = "Content-Type";
    private static final String CONTENT_TYPE_VALUE = "text/plain";
    private final LBRegistry registry;
    private final ErrorBuilder errorBuilder;

    @Override
    public Response from(@NonNull final Request request) {
        try {
            String body = new String(request.body().readAllBytes());
            HostStatus hostStatus = HostStatus.from(body);
            registry.updateWorkerHost(hostStatus);

            return new Response(
                    StatusCode.OK,
                    Map.of(CONTENT_TYPE_NAME, CONTENT_TYPE_VALUE),
                    InputStream.nullInputStream()
            );
        } catch (Exception e) {
            log.error("Failed to update worker metrics: {}", e.getMessage());
            return errorBuilder.from(request, StatusCode.BAD_REQUEST);
        }
    }
}
