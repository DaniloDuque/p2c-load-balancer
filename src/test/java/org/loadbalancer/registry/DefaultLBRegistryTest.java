package org.loadbalancer.registry;

import org.core.HostMetadata;
import org.core.metric.HostStatus;
import org.core.metric.MetricName;
import org.core.metric.MetricValue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DefaultLBRegistryTest {

    private DefaultLBRegistry registry;
    private Selector mockSelector;
    private ConcurrentHashMap<HostMetadata, Collection<MetricValue>> registryMap;

    @BeforeEach
    void setUp() {
        mockSelector = mock(Selector.class);
        registryMap = new ConcurrentHashMap<>();
        
        registry = DefaultLBRegistry.builder()
                .registryMap(registryMap)
                .selector(mockSelector)
                .build();
    }

    @Test
    void shouldReturnNullWhenNoWorkers() {
        HostMetadata result = registry.getNextLoadBalancedHost();
        
        assertNull(result);
        verify(mockSelector, never()).getNextHost(any());
    }

    @Test
    void shouldDelegateToSelector() {
        // Add a worker to make the map non-empty
        HostMetadata host = new HostMetadata("localhost", 8081);
        List<MetricValue> metrics = List.of(
                new MetricValue(MetricName.CPU_UTILIZATION, 0.5)
        );
        registryMap.put(host, metrics);
        
        HostMetadata expectedHost = new HostMetadata("localhost", 8082);
        when(mockSelector.getNextHost(registryMap)).thenReturn(expectedHost);
        
        HostMetadata result = registry.getNextLoadBalancedHost();
        
        assertEquals(expectedHost, result);
        verify(mockSelector).getNextHost(registryMap);
    }

    @Test
    void shouldUpdateWorkerHost() {
        HostMetadata host = new HostMetadata("localhost", 8081);
        List<MetricValue> metrics = List.of(
                new MetricValue(MetricName.CPU_UTILIZATION, 0.5),
                new MetricValue(MetricName.NUMBER_OF_REQUESTS, 10L)
        );
        HostStatus hostStatus = HostStatus.of(host, metrics);
        
        registry.updateWorkerHost(hostStatus);
        
        assertTrue(registryMap.containsKey(host));
        assertEquals(metrics, registryMap.get(host));
    }

    @Test
    void shouldOverwriteExistingWorkerMetrics() {
        HostMetadata host = new HostMetadata("localhost", 8081);
        
        List<MetricValue> oldMetrics = List.of(
                new MetricValue(MetricName.CPU_UTILIZATION, 0.8)
        );
        List<MetricValue> newMetrics = List.of(
                new MetricValue(MetricName.CPU_UTILIZATION, 0.2)
        );
        
        registry.updateWorkerHost(HostStatus.of(host, oldMetrics));
        registry.updateWorkerHost(HostStatus.of(host, newMetrics));
        
        assertEquals(newMetrics, registryMap.get(host));
        assertEquals(1, registryMap.size());
    }
}