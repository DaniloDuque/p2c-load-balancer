package org.worker;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.core.Config;
import org.core.HttpServer;
import org.core.ServerConfig;
import org.core.resource.DefaultResource;
import org.core.resource.Resource;

import java.io.IOException;
import java.util.OptionalInt;

@Log4j2
public final class WorkerApplication {
    private static final int ARGS_COUNT = 2;
    private static final String DEFAULT_ERRORS_PATH
            = "src/main/resources/errors";
    private static final int MINIMUM_PORT = 1;
    private static final int MAXIMUM_PORT = 65535;

    private WorkerApplication() {
    }

    public static void main(final String... args) {
        if (!validateArgsCount(args)) {
            return;
        }

        final OptionalInt port = validatePort(args[0]);
        if (port.isEmpty()) {
            return;
        }

        final String errorsPath = args.length > 1
                ? args[1] : DEFAULT_ERRORS_PATH;
        final DefaultResource errorsDirectory
                = new DefaultResource(errorsPath);

        try {
            ensureErrorsDirectory(errorsDirectory);

            Config config = WorkerConfig.builder()
                    .errorsDirectory(errorsDirectory)
                    .build();

            ServerConfig serverConfig = ServerConfig.builder()
                    .port(port.getAsInt())
                    .build();

            HttpServer server = HttpServer.builder()
                    .config(serverConfig)
                    .filters(config.getServerFilters())
                    .handlers(config.getServerHandlers())
                    .build();

            Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
            server.start();

        } catch (Exception e) {
            log.error("Failed to start server: {}", e.getMessage(), e);
        }
    }

    private static void ensureErrorsDirectory(
            @NonNull final Resource errorsDirectory) throws IOException {
        if (!errorsDirectory.exists()) {
            log.error("Errors directory does not exist: {}",
                    errorsDirectory.getPath()
            );
            log.info("Creating errors directory...");
            java.nio.file.Files.createDirectories(errorsDirectory.getPath());
        }
    }

    private static OptionalInt validatePort(final String portArg) {
        try {
            final int port = Integer.parseUnsignedInt(portArg);
            if (port < MINIMUM_PORT || port > MAXIMUM_PORT) {
                log.error("Port must be between 1 and 65535");
                return OptionalInt.empty();
            }
            return OptionalInt.of(port);
        } catch (NumberFormatException nfe) {
            log.error("Argument <port> should be a number");
            return OptionalInt.empty();
        }
    }

    private static boolean validateArgsCount(final String[] args) {
        if (args.length < 1 || args.length > ARGS_COUNT) {
            printUsage();
            log.error("Invalid number of arguments: {}", args.length);
            return false;
        }
        return true;
    }

    private static void printUsage() {
        log.info("Usage: http <port number> [errors path]");
    }
}
