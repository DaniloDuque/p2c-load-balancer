package org.loadbalancer.client;

import org.core.HostMetadata;
import org.core.request.Request;
import org.core.response.Response;

public interface WorkerClient {
    Response send(HostMetadata hostMetadata, Request request) throws Exception;
}
