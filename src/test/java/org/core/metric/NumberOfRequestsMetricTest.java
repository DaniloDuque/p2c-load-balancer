package org.core.metric;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NumberOfRequestsMetricTest {

    @Test
    void shouldStartWithZero() {
        NumberOfRequestsMetric metric = new NumberOfRequestsMetric();
        
        MetricValue value = metric.getValue();
        
        assertEquals(MetricName.NUMBER_OF_REQUESTS, value.name());
        assertEquals(0L, value.value());
    }

    @Test
    void shouldUpdateValue() {
        NumberOfRequestsMetric metric = new NumberOfRequestsMetric();
        
        metric.update(5);
        MetricValue value = metric.getValue();
        
        assertEquals(5L, value.value());
    }

    @Test
    void shouldAccumulateUpdates() {
        NumberOfRequestsMetric metric = new NumberOfRequestsMetric();
        
        metric.update(3);
        metric.update(7);
        metric.update(2);
        
        MetricValue value = metric.getValue();
        assertEquals(12L, value.value());
    }

    @Test
    void shouldHandleNegativeUpdates() {
        NumberOfRequestsMetric metric = new NumberOfRequestsMetric();
        
        metric.update(10);
        metric.update(-3);
        
        MetricValue value = metric.getValue();
        assertEquals(7L, value.value());
    }
}