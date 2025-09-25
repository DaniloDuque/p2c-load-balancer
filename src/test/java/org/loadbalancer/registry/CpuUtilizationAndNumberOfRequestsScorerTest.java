package org.loadbalancer.registry;

import org.core.metric.MetricName;
import org.core.metric.MetricValue;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class CpuUtilizationAndNumberOfRequestsScorerTest {

    private final CpuUtilizationAndNumberOfRequestsScorer scorer = 
            new CpuUtilizationAndNumberOfRequestsScorer();

    @Test
    void shouldScoreWithBothMetrics() {
        List<MetricValue> metrics = List.of(
                new MetricValue(MetricName.CPU_UTILIZATION, 0.5),
                new MetricValue(MetricName.NUMBER_OF_REQUESTS, 10L)
        );

        double score = scorer.score(metrics);
        
        assertEquals(1.0 / (0.5 * 10 + 1), score, 0.001);
    }

    @Test
    void shouldUseDefaultsForMissingMetrics() {
        List<MetricValue> metrics = List.of();

        double score = scorer.score(metrics);
        
        assertEquals(1.0 / (1 * 1e10 + 1), score, 0.001);
    }

    @Test
    void shouldScoreWithOnlyCpuMetric() {
        List<MetricValue> metrics = List.of(
                new MetricValue(MetricName.CPU_UTILIZATION, 0.2)
        );

        double score = scorer.score(metrics);
        
        assertEquals(1.0 / (0.2 * 1e10 + 1), score, 0.001);
    }

    @Test
    void shouldScoreWithOnlyRequestMetric() {
        List<MetricValue> metrics = List.of(
                new MetricValue(MetricName.NUMBER_OF_REQUESTS, 5L)
        );

        double score = scorer.score(metrics);
        
        assertEquals(1.0 / (1 * 5 + 1), score, 0.001);
    }
}