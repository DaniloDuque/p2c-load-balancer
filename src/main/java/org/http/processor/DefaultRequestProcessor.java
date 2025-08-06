package org.http.processor;

import lombok.Builder;
import lombok.NonNull;
import org.http.StatusCode;
import org.http.model.error.ErrorBuilder;
import org.http.model.request.Method;
import org.http.model.request.Request;
import org.http.model.response.Response;
import org.http.model.response.ResponseBuilder;

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
