package org.worker.core.genetic;

import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.BitSet;
import java.util.Objects;

@Getter
public final class Entity implements Comparable<Entity> {

    private static final int MUTATION_PROBABILITY_PERCENT = 5;
    private static final int MAX_PROBABILITY = 100;

    private final List<Integer> board;
    private final int size;
    private final Random random;
    private int fitness;

    public Entity(final int boardSize,
                  @NonNull final Random randomNumberGenerator) {
        this.size = boardSize;
        this.random = randomNumberGenerator;
        this.board = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            board.add(i);
        }
        Collections.shuffle(board, random);
        calculateFitness();
    }

    public Entity(@NonNull final Entity parent1,
                  @NonNull final Entity parent2) {
        this.size = parent1.size;
        this.random = parent1.random;
        this.board = crossover(parent1, parent2);
        maybeMutate();
        calculateFitness();
    }

    private List<Integer> crossover(@NonNull final Entity p1,
                                    @NonNull final Entity p2) {
        BitSet used = new BitSet(size);
        List<Integer> childBoard = new ArrayList<>(size);
        int cut = random.nextInt(size);

        for (int i = 0; i < cut; i++) {
            int gene = p1.board.get(i);
            childBoard.add(gene);
            used.set(gene);
        }

        for (int gene : p2.board) {
            if (!used.get(gene)) {
                childBoard.add(gene);
            }
        }

        return childBoard;
    }

    private void maybeMutate() {
        if (random.nextInt(MAX_PROBABILITY) < MUTATION_PROBABILITY_PERCENT) {
            int i = random.nextInt(size);
            int j = random.nextInt(size);
            Collections.swap(board, i, j);
        }
    }

    private void calculateFitness() {
        int conflicts = 0;
        for (int col1 = 0; col1 < size; col1++) {
            int row1 = board.get(col1);
            for (int col2 = col1 + 1; col2 < size; col2++) {
                int row2 = board.get(col2);
                if (Math.abs(col1 - col2) == Math.abs(row1 - row2)) {
                    conflicts++;
                }
            }
        }
        this.fitness = conflicts * conflicts + 2;
    }

    @Override
    public int compareTo(@NonNull final Entity other) {
        return Integer.compare(this.fitness, other.fitness);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Entity other)) {
            return false;
        }
        return fitness == other.fitness && Objects.equals(board, other.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, fitness);
    }
}
