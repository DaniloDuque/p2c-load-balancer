package org.core.client;

import org.core.model.request.Request;
import org.core.model.response.Response;

public interface Client {
    Response execute(Request request);
}
