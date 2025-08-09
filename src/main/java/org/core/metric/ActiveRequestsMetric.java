package org.core.metric;

import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.concurrent.atomic.AtomicLong;

@NoArgsConstructor
public final class ActiveRequestsMetric implements UpdatableMetric<Integer> {
    private final AtomicLong activeCount = new AtomicLong(0);

    @Override
    public void update(@NonNull final Integer value) {
    }

    @Override
    public Integer getValue() {
        return (int) activeCount.get();
    }

    @Override
    public void reset() {
        activeCount.set(0);
    }

    @Override
    public String toString() {
        return String.format("{%s,%d}",
                MetricName.ACTIVE_REQUESTS,
                activeCount.get()
        );
    }
}
