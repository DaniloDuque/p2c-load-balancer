package org.worker;

import com.sun.net.httpserver.HttpHandler;
import lombok.Builder;
import org.http.Config;
import org.http.handler.GenericHandler;
import org.http.model.error.DefaultErrorBuilder;
import org.http.model.error.ErrorBuilder;
import org.http.model.request.Method;
import org.http.model.resource.Resource;
import org.http.model.response.ResponseBuilder;
import org.http.parser.DefaultInputParser;
import org.http.parser.InputParser;
import org.http.processor.DefaultRequestProcessor;
import org.http.processor.RequestProcessor;
import org.worker.core.NQueenResponse;
import org.worker.core.Solver;
import org.worker.core.genetic.GeneticNQueenSolver;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

@Builder
public final class WorkerConfig implements Config {
    private static final String NQUEEN_SOLVER_PATH = "/solver/queen";
    private static final String SERVER_NAME = "Worker-Server";
    private static final String DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss 'GMT'";
    private static final Locale LOCALE_FORMAT = Locale.ENGLISH;
    private static final String TIME_ZONE = "GMT";

    private final Resource errorsDirectory;
    private RequestProcessor requestProcessor;
    private Map<Method, ResponseBuilder> responseBuilders;
    private InputParser inputParser;
    private Solver problemSolver;
    private ErrorBuilder errorBuilder;

    private static SimpleDateFormat simpleDateFormat() {
        final SimpleDateFormat httpDateFormat = new SimpleDateFormat(
                DATE_FORMAT,
                LOCALE_FORMAT
        );
        httpDateFormat.setTimeZone(TimeZone.getTimeZone(TIME_ZONE));
        return httpDateFormat;
    }

    public Map<String, HttpHandler> getServerHandlers() {
        Map<String, HttpHandler> handlers = new HashMap<>();
        handlers.put(NQUEEN_SOLVER_PATH,
                new GenericHandler(inputParser(), requestProcessor()));
        return handlers;
    }

    private RequestProcessor requestProcessor() {
        if (requestProcessor == null) {
            requestProcessor = DefaultRequestProcessor.builder()
                    .responseBuilders(responseBuilders())
                    .errorBuilder(errorBuilder())
                    .build();
        }
        return requestProcessor;
    }

    private InputParser inputParser() {
        if (inputParser == null) {
            inputParser = new DefaultInputParser();
        }
        return inputParser;
    }

    private Solver problemSolver() {
        if (problemSolver == null) {
            problemSolver = new GeneticNQueenSolver();
        }
        return problemSolver;
    }

    private ErrorBuilder errorBuilder() {
        if (errorBuilder == null) {
            errorBuilder = new DefaultErrorBuilder(
                    errorsDirectory,
                    SERVER_NAME,
                    simpleDateFormat()
            );
        }
        return errorBuilder;
    }

    private String serverName() {
        return SERVER_NAME;
    }

    private Map<Method, ResponseBuilder> responseBuilders() {
        if (responseBuilders == null) {
            responseBuilders = Map.of(
                    Method.GET, NQueenResponse
                            .builder()
                            .errorBuilder(errorBuilder())
                            .serverName(serverName())
                            .dateFormat(simpleDateFormat())
                            .solver(problemSolver())
                            .build()
            );
        }
        return responseBuilders;
    }
}
