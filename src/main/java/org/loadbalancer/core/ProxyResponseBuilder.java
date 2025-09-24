package org.loadbalancer.core;

import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.core.StatusCode;
import org.loadbalancer.client.WorkerClient;
import org.loadbalancer.registry.LBRegistry;
import org.core.error.ErrorBuilder;
import org.core.request.Request;
import org.core.response.Response;
import org.core.response.ResponseBuilder;

@Slf4j
@Builder
public final class ProxyResponseBuilder implements ResponseBuilder {
    private final ErrorBuilder errorBuilder;
    private final LBRegistry registry;
    private final WorkerClient client;

    @Override
    public Response from(@NonNull final Request request) {
        val hostMetadata = registry.getNextLoadBalancedHost();
        if (hostMetadata == null) {
            return errorBuilder.from(
                    request,
                    StatusCode.SERVICE_UNAVAILABLE
            );
        }
        try {
            return client.send(hostMetadata, request);
        } catch (Exception e) {
            log.warn("Worker {}:{} failed to process request: {}",
                    hostMetadata.host(), hostMetadata.port(), e.getMessage());
            return errorBuilder.from(request, StatusCode.INTERNAL_SERVER_ERROR);
        }
    }
}
