package org.worker.client;

import org.core.HostMetadata;
import org.core.metric.Metric;

import java.net.http.HttpRequest;
import java.util.Collection;

public interface MetricRequestAdapter {
    HttpRequest adapt(HostMetadata hostMetadata, Collection<Metric<?>> metrics);
}
