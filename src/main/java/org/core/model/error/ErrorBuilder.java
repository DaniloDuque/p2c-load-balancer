package org.core.model.error;

import org.core.StatusCode;
import org.core.model.request.Request;
import org.core.model.response.Response;

public interface ErrorBuilder {
    Response from(Request request, StatusCode status);
}
