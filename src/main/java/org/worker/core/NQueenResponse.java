package org.worker.core;

import lombok.Builder;
import lombok.NonNull;
import lombok.val;
import org.http.StatusCode;
import org.http.model.error.ErrorBuilder;
import org.http.model.request.Request;
import org.http.model.response.Response;
import org.http.model.response.ResponseBuilder;
import org.http.model.response.ResponseUtils;

import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Optional;

@Builder
public final class NQueenResponse implements ResponseBuilder {
    private static final String DATE = "Date";
    private static final String SERVER = "Server";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String CONNECTION = "Connection";
    private static final String CONNECTION_STATUS = "keep-alive";
    private static final String BOARD_SIZE_QUERY_PARAMETER = "boardSize";
    private static final String CONTENT_TYPE_VALUE = "text/plain";

    private final ErrorBuilder errorBuilder;
    private final String serverName;
    private final SimpleDateFormat dateFormat;
    private final Solver solver;

    private Optional<Integer> getBoardSizeFromPath(final String path) {
        if (path == null || !path.contains("?")) {
            return Optional.empty();
        }

        String query = path.substring(path.indexOf("?") + 1);
        String[] params = query.split("&");

        for (String param : params) {
            String[] keyValue = param.split("=");
            if (keyValue.length == 2
                    && BOARD_SIZE_QUERY_PARAMETER.equals(keyValue[0])) {
                try {
                    return Optional.of(Integer.parseInt(keyValue[1]));
                } catch (NumberFormatException e) {
                    return Optional.empty();
                }
            }
        }

        return Optional.empty();
    }

    private Map<String, String> createHeaders(final int contentLength) {
        return ResponseUtils.createCommonHeaders(
                serverName,
                dateFormat,
                CONTENT_TYPE_VALUE,
                contentLength
        );
    }

    @Override
    public Response from(@NonNull final Request request) {
        try {
            if (!request.path().contains(BOARD_SIZE_QUERY_PARAMETER)) {
                return errorBuilder.from(request, StatusCode.BAD_REQUEST);
            }

            val boardSize = getBoardSizeFromPath(request.path());
            if (boardSize.isEmpty()) {
                return errorBuilder.from(request, StatusCode.BAD_REQUEST);
            }

            val solution = solver.solve(boardSize.get());
            if (solution.isEmpty()) {
                return errorBuilder.from(request, StatusCode.BAD_REQUEST);
            }

            StringBuilder solutionText = new StringBuilder();
            solutionText.append("Positions: ");
            for (int pos : solution.get().positions()) {
                solutionText.append(pos).append(" ");
            }
            solutionText
                    .append("\nCollisions: ")
                    .append(solution.get().collisions());

            byte[] responseBody = solutionText.toString().getBytes();
            java.io.InputStream body = new java.io
                    .ByteArrayInputStream(responseBody);

            Map<String, String> headers = createHeaders(responseBody.length);

            return new Response(StatusCode.OK, headers, body);

        } catch (Exception e) {
            return errorBuilder.from(request, StatusCode.INTERNAL_SERVER_ERROR);
        }
    }
}
