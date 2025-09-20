package org.core.response;

import org.core.request.Request;

public interface ResponseBuilder {
    Response from(Request request);
}
