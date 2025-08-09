package org.core.metric;

import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.concurrent.atomic.AtomicLong;

@NoArgsConstructor
public final class NumberOfRequestsMetric implements UpdatableMetric<Integer> {
    private final AtomicLong activeCount = new AtomicLong(0);

    @Override
    public void update(@NonNull final Integer value) {
        activeCount.addAndGet(value);
    }

    @Override
    public Integer getValue() {
        return (int) activeCount.get();
    }

    @Override
    public String toString() {
        return String.format("[%s,%d]",
                MetricName.NUMBER_OF_REQUESTS,
                activeCount.get()
        );
    }
}
