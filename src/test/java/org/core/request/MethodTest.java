package org.core.request;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MethodTest {

    @Test
    void shouldParseValidMethods() {
        assertEquals(Method.GET, Method.from("GET"));
        assertEquals(Method.POST, Method.from("POST"));
        assertEquals(Method.PUT, Method.from("PUT"));
        assertEquals(Method.DELETE, Method.from("DELETE"));
        assertEquals(Method.HEAD, Method.from("HEAD"));
        assertEquals(Method.OPTIONS, Method.from("OPTIONS"));
    }

    @Test
    void shouldBeCaseInsensitive() {
        assertEquals(Method.GET, Method.from("get"));
        assertEquals(Method.POST, Method.from("post"));
        assertEquals(Method.PUT, Method.from("put"));
    }

    @Test
    void shouldReturnErrorForInvalidMethod() {
        assertEquals(Method.ERROR, Method.from("INVALID"));
        assertEquals(Method.ERROR, Method.from(""));
        assertEquals(Method.ERROR, Method.from("UNKNOWN"));
    }

    @Test
    void shouldDetermineIfResponseBodyNeeded() {
        assertTrue(Method.needsResponseBody(Method.GET));
        assertTrue(Method.needsResponseBody(Method.POST));
        assertTrue(Method.needsResponseBody(Method.PUT));
        assertFalse(Method.needsResponseBody(Method.HEAD));
    }
}