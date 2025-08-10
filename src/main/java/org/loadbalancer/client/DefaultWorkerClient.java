package org.loadbalancer.client;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.core.HostMetadata;
import org.core.StatusCode;
import org.model.request.Request;
import org.model.response.Response;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
public final class DefaultWorkerClient implements WorkerClient {
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Override
    public Response send(
            @NonNull final HostMetadata hostMetadata,
            @NonNull final Request request) throws Exception {
        URI uri = URI.create(String.format("http://%s:%d%s",
            hostMetadata.host(), hostMetadata.port(), request.path()));

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(uri)
                .method(request.method().name(),
                       request.body() != null
                       ? HttpRequest.BodyPublishers.ofInputStream(request::body)
                       : HttpRequest.BodyPublishers.noBody());

        request.headers().forEach(builder::header);

        HttpResponse<byte[]> response = httpClient.send(
                builder.build(),
                HttpResponse.BodyHandlers.ofByteArray());

        Map<String, String> headers = new HashMap<>();
        response.headers().map().forEach((key, values) ->
            headers.put(key, String.join(", ", values)));

        return new Response(
                StatusCode.valueOf(response.statusCode()),
                headers,
                new ByteArrayInputStream(response.body())
        );
    }
}
