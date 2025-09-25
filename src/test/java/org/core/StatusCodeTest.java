package org.core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StatusCodeTest {

    @Test
    void shouldReturnCorrectStatusCode() {
        assertEquals(200, StatusCode.OK.getStatusCode());
        assertEquals(400, StatusCode.BAD_REQUEST.getStatusCode());
        assertEquals(404, StatusCode.NOT_FOUND.getStatusCode());
        assertEquals(500, StatusCode.INTERNAL_SERVER_ERROR.getStatusCode());
        assertEquals(503, StatusCode.SERVICE_UNAVAILABLE.getStatusCode());
    }

    @Test
    void shouldReturnStatusCodeFromInt() {
        assertEquals(StatusCode.OK, StatusCode.valueOf(200));
        assertEquals(StatusCode.BAD_REQUEST, StatusCode.valueOf(400));
        assertEquals(StatusCode.NOT_FOUND, StatusCode.valueOf(404));
        assertEquals(StatusCode.INTERNAL_SERVER_ERROR, StatusCode.valueOf(500));
    }

    @Test
    void shouldReturnNullForUnknownStatusCode() {
        assertNull(StatusCode.valueOf(999));
        assertNull(StatusCode.valueOf(100));
    }
}