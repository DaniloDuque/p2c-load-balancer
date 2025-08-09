package org.model.response;

import org.model.request.Request;

public interface ResponseBuilder {
    Response from(Request request);
}
