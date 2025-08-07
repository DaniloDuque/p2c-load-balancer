package org.core.model.resource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public interface Resource {
    boolean exists();

    Path getPath();

    InputStream openStream() throws IOException;

    long length() throws IOException;
}
