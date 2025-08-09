package org.core.processor;

import lombok.Builder;
import lombok.NonNull;
import org.core.StatusCode;
import org.model.error.ErrorBuilder;
import org.model.request.Method;
import org.model.request.Request;
import org.model.response.Response;
import org.model.response.ResponseBuilder;

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
