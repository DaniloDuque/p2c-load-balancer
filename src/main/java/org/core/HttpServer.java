package org.core;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpHandler;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Builder
public final class HttpServer {
    @NonNull
    private final ServerConfig config;

    @NonNull
    private final Map<String, HttpHandler> handlers;

    @NonNull
    private final Map<String, Collection<Filter>> filters;

    private ExecutorService threadPool;
    private com.sun.net.httpserver.HttpServer server;
    private volatile boolean running;

    public void start() {
        if (running) {
            log.warn("Server is already running");
            return;
        }

        running = true;
        threadPool = Executors.newFixedThreadPool(config.getThreadPoolSize());
        log.info("Starting HTTP server on port: {}", config.getPort());

        try {
            server = com.sun.net.httpserver.HttpServer.create(
                    new InetSocketAddress(config.getPort()),
                    0
            );
            for (Map.Entry<String, HttpHandler> entry : handlers.entrySet()) {
                var context = server.createContext(
                        entry.getKey(),
                        entry.getValue()
                );

                Collection<Filter> contextFilters = filters.get(entry.getKey());
                if (contextFilters != null) {
                    context.getFilters().addAll(contextFilters);
                }
            }

            server.setExecutor(threadPool);
            server.start();
        } catch (IOException e) {
            log.error("Could not listen on port {}: {}",
                    config.getPort(),
                    e.getMessage()
            );
            stop();
        }
    }

    public void stop() {
        if (!running) {
            return;
        }

        running = false;
        log.info("Stopping HTTP server...");

        if (server != null) {
            server.stop(config.getShutdownTimeoutSeconds());
        }

        if (threadPool != null && !threadPool.isShutdown()) {
            threadPool.shutdown();
            try {
                if (!threadPool.awaitTermination(
                        config.getShutdownTimeoutSeconds(),
                        TimeUnit.SECONDS
                )) {
                    threadPool.shutdownNow();
                }
            } catch (InterruptedException e) {
                threadPool.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        log.info("HTTP server stopped");
    }
}
