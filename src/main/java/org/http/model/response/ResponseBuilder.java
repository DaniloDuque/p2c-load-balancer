package org.http.model.response;

import org.http.model.request.Request;

public interface ResponseBuilder {
    Response from(Request request);
}
