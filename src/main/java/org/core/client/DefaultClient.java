package org.core.client;

import lombok.Builder;
import lombok.NonNull;
import org.core.model.request.Request;
import org.core.model.response.Response;

@Builder
public final class DefaultClient implements Client {
    @NonNull
    private final HostMetadata hostMetadata;


    @Override
    public Response execute(@NonNull final Request request) {
        return null;
    }
}
