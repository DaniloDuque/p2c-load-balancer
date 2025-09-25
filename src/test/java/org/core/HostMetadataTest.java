package org.core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HostMetadataTest {

    @Test
    void shouldCreateHostMetadata() {
        HostMetadata host = new HostMetadata("localhost", 8080);
        
        assertEquals("localhost", host.host());
        assertEquals(8080, host.port());
    }

    @Test
    void shouldSerializeToString() {
        HostMetadata host = new HostMetadata("localhost", 8080);
        
        assertEquals("[localhost,8080]", host.toString());
    }

    @Test
    void shouldDeserializeFromString() {
        String serialized = "[localhost,8080]";
        
        HostMetadata host = HostMetadata.from(serialized);
        
        assertEquals("localhost", host.host());
        assertEquals(8080, host.port());
    }

    @Test
    void shouldThrowExceptionForInvalidFormat() {
        assertThrows(Exception.class, () -> HostMetadata.from("invalid"));
        assertThrows(Exception.class, () -> HostMetadata.from("[localhost]"));
        assertThrows(Exception.class, () -> HostMetadata.from("[localhost,invalid]"));
    }
}