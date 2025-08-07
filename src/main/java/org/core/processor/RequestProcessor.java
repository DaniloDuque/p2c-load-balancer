package org.core.processor;

import org.core.model.request.Request;
import org.core.model.response.Response;

public interface RequestProcessor {
    Response process(Request request);
}
