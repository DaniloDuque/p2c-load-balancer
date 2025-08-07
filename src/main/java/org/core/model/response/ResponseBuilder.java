package org.core.model.response;

import org.core.model.request.Request;

public interface ResponseBuilder {
    Response from(Request request);
}
