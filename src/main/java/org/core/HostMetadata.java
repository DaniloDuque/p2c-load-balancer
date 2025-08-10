package org.core;

import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

public record HostMetadata(String host, Integer port) {

    public static HostMetadata from(@NonNull final String serialized) {
        return new HostMetadata(
                serialized.split(",")[0],
                Integer.parseInt(serialized.split(",")[1])
        );
    }

    @NotNull
    @Override
    public String toString() {
        return String.format("[%s,%d]", host, port);
    }
}
