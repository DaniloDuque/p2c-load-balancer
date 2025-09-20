package org.core.processor;

import org.core.request.Request;
import org.core.response.Response;

public interface RequestProcessor {
    Response process(Request request);
}
