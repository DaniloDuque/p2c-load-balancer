package org.core.resource;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Log4j2
public final class DefaultResource implements Resource {

    private final Path resourcePath;

    public DefaultResource(@NonNull final Resource resource,
                           @NonNull final String relativePath) {
        this.resourcePath = buildPath(resource, relativePath);
    }

    public DefaultResource(@NonNull final String path) {
        this.resourcePath = Paths.get(path);
    }

    private Path buildPath(@NonNull final Resource resource,
                           @NonNull final String relativePath) {
        return Paths.get(resource.getPath().toString(), relativePath);
    }

    @Override
    public boolean exists() {
        return Files.exists(resourcePath);
    }

    @Override
    public Path getPath() {
        return resourcePath;
    }

    @Override
    public InputStream openStream() throws IOException {
        return Files.newInputStream(resourcePath);
    }

    @Override
    public long length() throws IOException {
        return Files.size(resourcePath);
    }
}
