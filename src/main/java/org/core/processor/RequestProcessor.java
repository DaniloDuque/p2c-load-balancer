package org.core.processor;

import org.model.request.Request;
import org.model.response.Response;

public interface RequestProcessor {
    Response process(Request request);
}
