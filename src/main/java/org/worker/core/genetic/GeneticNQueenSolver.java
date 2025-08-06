package org.worker.core.genetic;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.worker.core.Solver;
import org.worker.core.SolverResult;

import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Random;

@NoArgsConstructor
public final class GeneticNQueenSolver implements Solver {

    private static final int MINIMUM_POSSIBLE_SIZE = 4;

    private static Entity selectParent(final Entity[] population,
                                       final double target) {
        double cumulativeFitness = 0.0;
        int j = 0;

        while (cumulativeFitness < target && j < population.length) {
            cumulativeFitness += totalFitness(population[j++]);
        }

        return population[Math.max(j - 1, 0)];
    }

    private static double totalFitness(@NonNull final Entity entity) {
        return 1.0 / entity.getFitness();
    }

    @Override
    public Optional<SolverResult> solve(final int boardSize) {
        if (boardSize < MINIMUM_POSSIBLE_SIZE) {
            return Optional.empty();
        }

        Entity bestSolution = runGeneticAlgorithm(boardSize);
        int[] positions = bestSolution
                .getBoard()
                .stream()
                .mapToInt(Integer::intValue)
                .toArray();
        int collisions = calculateCollisions(positions);

        return Optional.of(new SolverResult(positions, collisions));
    }

    private int calculateCollisions(final int[] board) {
        int conflicts = 0;
        for (int col1 = 0; col1 < board.length; col1++) {
            int row1 = board[col1];
            for (int col2 = col1 + 1; col2 < board.length; col2++) {
                int row2 = board[col2];
                if (Math.abs(col1 - col2) == Math.abs(row1 - row2)) {
                    conflicts++;
                }
            }
        }
        return conflicts;
    }

    private Entity runGeneticAlgorithm(final int boardSize) {
        final int populationSize = boardSize * 3;
        Entity[] population = new Entity[populationSize];
        PriorityQueue<Entity> heap = new PriorityQueue<>();
        Random random = new Random();

        for (int i = 0; i < populationSize; i++) {
            population[i] = new Entity(boardSize, random);
            heap.add(population[i]);
        }

        Entity best = heap.peek();
        int stagnationCounter = 0;
        double genSum = 0.0;

        for (int i = 0; i < populationSize; i++) {
            genSum += totalFitness(population[i]);
        }

        while (stagnationCounter < boardSize * boardSize
                && best.getFitness() > 2) {
            for (int i = 1; i < populationSize; i++) {
                double target1 = random.nextDouble() * genSum;
                double target2 = random.nextDouble() * genSum;

                Entity parent1 = selectParent(population, target1);
                Entity parent2 = selectParent(population, target2);

                Entity offspring = new Entity(parent1, parent2);
                heap.add(offspring);
                heap.add(population[i]);
            }

            for (int i = 0; i < populationSize; i++) {
                population[i] = heap.poll();
            }

            genSum = 0.0;
            for (int i = 0; i < populationSize; i++) {
                genSum += totalFitness(population[i]);
            }

            Entity currentBest = population[0];
            stagnationCounter = currentBest.equals(best)
                    ? stagnationCounter + 1 : 0;
            best = currentBest;
            heap.clear();
        }

        return best;
    }
}
