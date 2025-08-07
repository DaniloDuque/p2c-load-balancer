package org.core.processor;

import lombok.Builder;
import lombok.NonNull;
import org.core.StatusCode;
import org.core.model.error.ErrorBuilder;
import org.core.model.request.Method;
import org.core.model.request.Request;
import org.core.model.response.Response;
import org.core.model.response.ResponseBuilder;

import java.util.Map;

@Builder
public final class DefaultRequestProcessor implements RequestProcessor {

    private final Map<Method, ResponseBuilder> responseBuilders;
    private final ErrorBuilder errorBuilder;

    @Override
    public Response process(@NonNull final Request request) {
        return responseBuilders.containsKey(request.method())
                ? responseBuilders.get(request.method()).from(request)
                : errorBuilder.from(request, StatusCode.NOT_IMPLEMENTED);
    }
}
