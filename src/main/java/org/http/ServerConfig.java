package org.http;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ServerConfig {
    private static final int DEFAULT_THREAD_POOL_SIZE = 25;
    private static final int DEFAULT_SHUTDOWN_TIMEOUT = 25;

    @Builder.Default
    private final int threadPoolSize = DEFAULT_THREAD_POOL_SIZE;

    @Builder.Default
    private final int shutdownTimeoutSeconds = DEFAULT_SHUTDOWN_TIMEOUT;

    private final int port;
}
