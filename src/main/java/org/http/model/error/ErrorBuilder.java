package org.http.model.error;

import org.http.StatusCode;
import org.http.model.request.Request;
import org.http.model.response.Response;

public interface ErrorBuilder {
    Response from(Request request, StatusCode status);
}
