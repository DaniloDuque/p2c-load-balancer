package org.http.processor;

import org.http.model.request.Request;
import org.http.model.response.Response;

public interface RequestProcessor {
    Response process(Request request);
}
