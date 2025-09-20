package org.core.error;

import org.core.StatusCode;
import org.core.request.Request;
import org.core.response.Response;

public interface ErrorBuilder {
    Response from(Request request, StatusCode status);
}
