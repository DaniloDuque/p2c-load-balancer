package org.loadbalancer.core;

import lombok.Builder;
import lombok.NonNull;
import org.loadbalancer.registry.LBRegistry;
import org.model.request.Request;
import org.model.response.Response;
import org.model.response.ResponseBuilder;

@Builder
public final class UpdateWorkerMetricsResponseBuilder
        implements ResponseBuilder {
    private final LBRegistry registry;

    @Override
    public Response from(@NonNull final Request request) {
        return null;
    }
}
