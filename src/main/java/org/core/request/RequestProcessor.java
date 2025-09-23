package org.core.request;

import org.core.response.Response;

public interface RequestProcessor {
    Response process(Request request);
}
