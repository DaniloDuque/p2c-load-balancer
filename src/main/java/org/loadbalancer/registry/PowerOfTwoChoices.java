package org.loadbalancer.registry;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.val;
import org.core.HostMetadata;
import org.core.metric.HostStatus;
import org.core.metric.MetricValue;

import java.util.Collection;
import java.util.Random;
import java.util.concurrent.ConcurrentMap;

@AllArgsConstructor
public final class PowerOfTwoChoices implements Selector {
    private static final Random RANDOM = new java.util.Random();
    private final HostScorer hostScorer;

    record Pair<T, U>(T first, U second) {
    }

    private Pair<
            HostStatus,
            HostStatus
            > chooseTwo(
                    @NonNull final ConcurrentMap<
                            HostMetadata,
                            Collection<MetricValue>
                            > workerHostMap) {
        val entries = new java.util.ArrayList<>(workerHostMap.entrySet());
        val first = entries.get(RANDOM.nextInt(entries.size()));
        val second = entries.get(RANDOM.nextInt(entries.size()));
        return new Pair<>(
                HostStatus.of(first.getKey(), first.getValue()),
                HostStatus.of(second.getKey(), second.getValue())
        );
    }

    private HostMetadata getBest(
            @NonNull final Pair<
                    HostStatus,
                    HostStatus
                    > randomPair) {
        val firstScore = hostScorer.score(randomPair.first().metrics());
        val secondScore = hostScorer.score(randomPair.second().metrics());
        return (firstScore > secondScore
                ? randomPair.first()
                : randomPair.second()
        ).hostMetadata();
    }

    @Override
    public HostMetadata getNextHost(
            @NonNull final ConcurrentMap<
                    HostMetadata,
                    Collection<MetricValue>
                    > workerHostMap) {
        if (workerHostMap.isEmpty()) {
            return null;
        }
        val randomPair = chooseTwo(workerHostMap);
        return getBest(randomPair);
    }
}
