package org.loadbalancer.registry;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.val;
import org.core.HostMetadata;
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
            Pair<HostMetadata, Collection<MetricValue>>,
            Pair<HostMetadata, Collection<MetricValue>>
            > chooseTwo(
                    @NonNull final ConcurrentMap<
                            HostMetadata,
                            Collection<MetricValue>
                            > workerHostMap) {
        val entries = new java.util.ArrayList<>(workerHostMap.entrySet());
        val first = entries.get(RANDOM.nextInt(entries.size()));
        val second = entries.get(RANDOM.nextInt(entries.size()));
        return new Pair<>(
                new Pair<>(first.getKey(), first.getValue()),
                new Pair<>(second.getKey(), second.getValue())
        );
    }

    private HostMetadata getBest(
            @NonNull final Pair<
                    Pair<HostMetadata, Collection<MetricValue>>,
                    Pair<HostMetadata, Collection<MetricValue>>
                    > randomPair) {
        val firstScore = hostScorer.score(randomPair.first().second());
        val secondScore = hostScorer.score(randomPair.second().second());
        return (firstScore > secondScore
                ? randomPair.first()
                : randomPair.second()
        ).first();
    }

    @Override
    public HostMetadata getNextHost(
            @NonNull final ConcurrentMap<
                    HostMetadata,
                    Collection<MetricValue>
                    > workerHostMap) {
        val randomPair = chooseTwo(workerHostMap);
        return getBest(randomPair);
    }
}
