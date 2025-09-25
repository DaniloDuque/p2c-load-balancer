package org.core.metric;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MetricValueTest {

    @Test
    void shouldCreateMetricValue() {
        MetricValue metric = new MetricValue(MetricName.CPU_UTILIZATION, 0.75);
        
        assertEquals(MetricName.CPU_UTILIZATION, metric.name());
        assertEquals(0.75, metric.value());
    }

    @Test
    void shouldSerializeToString() {
        MetricValue metric = new MetricValue(MetricName.NUMBER_OF_REQUESTS, 42L);
        
        assertEquals("[NUMBER_OF_REQUESTS,42]", metric.toString());
    }

    @Test
    void shouldDeserializeFromString() {
        String serialized = "[CPU_UTILIZATION,0.5]";
        
        MetricValue metric = new MetricValue(serialized, Object.class);
        
        assertEquals(MetricName.CPU_UTILIZATION, metric.name());
        assertEquals("0.5", metric.value());
    }

    @Test
    void shouldHandleStringValues() {
        MetricValue metric = new MetricValue(MetricName.CPU_UTILIZATION, "test");
        
        assertEquals(MetricName.CPU_UTILIZATION, metric.name());
        assertEquals("test", metric.value());
        assertEquals("[CPU_UTILIZATION,test]", metric.toString());
    }
}