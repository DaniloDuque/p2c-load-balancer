package org.core;

import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

public record HostMetadata(String host, Integer port) {

    public static HostMetadata from(@NonNull final String serialized) {
        String content = serialized.substring(1, serialized.length() - 1);
        String[] parts = content.split(",");
        return new HostMetadata(
                parts[0],
                Integer.parseInt(parts[1])
        );
    }

    @NotNull
    @Override
    public String toString() {
        return String.format("[%s,%d]", host, port);
    }
}
