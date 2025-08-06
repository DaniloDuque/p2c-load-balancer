package org.http;

import com.sun.net.httpserver.HttpHandler;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Log4j2
@Builder
public final class HttpServer {
    @NonNull
    private final ServerConfig config;

    @NonNull
    private final Map<String, HttpHandler> handlers;

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
            handlers.forEach(server::createContext);
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
