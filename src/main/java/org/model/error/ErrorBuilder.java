package org.model.error;

import org.core.StatusCode;
import org.model.request.Request;
import org.model.response.Response;

public interface ErrorBuilder {
    Response from(Request request, StatusCode status);
}
