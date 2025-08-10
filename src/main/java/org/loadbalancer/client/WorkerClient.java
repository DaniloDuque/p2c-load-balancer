package org.loadbalancer.client;

import org.core.HostMetadata;
import org.model.request.Request;
import org.model.response.Response;

public interface WorkerClient {
    Response send(HostMetadata hostMetadata, Request request) throws Exception;
}
