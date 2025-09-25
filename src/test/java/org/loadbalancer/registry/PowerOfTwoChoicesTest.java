package org.loadbalancer.registry;

import org.core.HostMetadata;
import org.core.metric.MetricName;
import org.core.metric.MetricValue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PowerOfTwoChoicesTest {

    private PowerOfTwoChoices selector;
    private HostScorer mockScorer;
    private ConcurrentMap<HostMetadata, Collection<MetricValue>> workerMap;

    @BeforeEach
    void setUp() {
        mockScorer = mock(HostScorer.class);
        selector = new PowerOfTwoChoices(mockScorer);
        workerMap = new ConcurrentHashMap<>();
    }

    @Test
    void shouldReturnNullForEmptyMap() {
        HostMetadata result = selector.getNextHost(workerMap);
        
        assertNull(result);
    }

    @Test
    void shouldReturnOnlyHostWhenMapHasOne() {
        HostMetadata host = new HostMetadata("localhost", 8081);
        List<MetricValue> metrics = List.of(
                new MetricValue(MetricName.CPU_UTILIZATION, 0.5)
        );
        workerMap.put(host, metrics);
        
        when(mockScorer.score(metrics)).thenReturn(0.8);
        
        HostMetadata result = selector.getNextHost(workerMap);
        
        assertEquals(host, result);
    }

    @Test
    void shouldSelectHostFromAvailableHosts() {
        HostMetadata host1 = new HostMetadata("localhost", 8081);
        HostMetadata host2 = new HostMetadata("localhost", 8082);
        
        List<MetricValue> metrics1 = List.of(
                new MetricValue(MetricName.CPU_UTILIZATION, 0.8)
        );
        List<MetricValue> metrics2 = List.of(
                new MetricValue(MetricName.CPU_UTILIZATION, 0.2)
        );
        
        workerMap.put(host1, metrics1);
        workerMap.put(host2, metrics2);
        
        when(mockScorer.score(metrics1)).thenReturn(0.2);
        when(mockScorer.score(metrics2)).thenReturn(0.8);
        
        HostMetadata result = selector.getNextHost(workerMap);
        
        // Should return one of the available hosts
        assertTrue(result.equals(host1) || result.equals(host2));
        
        // Should have called scorer at least once
        verify(mockScorer, atLeastOnce()).score(any());
    }

    @Test
    void shouldCallScorerForSelectedHosts() {
        HostMetadata host1 = new HostMetadata("localhost", 8081);
        HostMetadata host2 = new HostMetadata("localhost", 8082);
        HostMetadata host3 = new HostMetadata("localhost", 8083);
        
        List<MetricValue> metrics = List.of(
                new MetricValue(MetricName.CPU_UTILIZATION, 0.5)
        );
        
        workerMap.put(host1, metrics);
        workerMap.put(host2, metrics);
        workerMap.put(host3, metrics);
        
        when(mockScorer.score(metrics)).thenReturn(0.5);
        
        HostMetadata result = selector.getNextHost(workerMap);
        
        assertNotNull(result);
        assertTrue(workerMap.containsKey(result));
        
        // Should call scorer exactly twice (for the two chosen hosts)
        verify(mockScorer, times(2)).score(metrics);
    }
}