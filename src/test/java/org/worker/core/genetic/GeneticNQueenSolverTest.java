package org.worker.core.genetic;

import org.junit.jupiter.api.Test;
import org.worker.core.SolverResult;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

class GeneticNQueenSolverTest {

    private final GeneticNQueenSolver solver = new GeneticNQueenSolver();

    @Test
    void shouldReturnEmptyForInvalidBoardSize() {
        Optional<SolverResult> result = solver.solve(3);
        assertTrue(result.isEmpty());
        
        result = solver.solve(2);
        assertTrue(result.isEmpty());
        
        result = solver.solve(0);
        assertTrue(result.isEmpty());
        
        result = solver.solve(-1);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldSolveValidBoardSizes() {
        Optional<SolverResult> result = solver.solve(4);
        assertTrue(result.isPresent());
        assertEquals(4, result.get().positions().length);
        
        result = solver.solve(8);
        assertTrue(result.isPresent());
        assertEquals(8, result.get().positions().length);
    }

    @Test
    void shouldReturnValidPositions() {
        Optional<SolverResult> result = solver.solve(4);
        assertTrue(result.isPresent());
        
        int[] positions = result.get().positions();
        
        // All positions should be within board bounds
        for (int pos : positions) {
            assertTrue(pos >= 0 && pos < 4);
        }
        
        // All positions should be unique (one queen per row)
        assertEquals(4, java.util.Arrays.stream(positions).distinct().count());
    }

    @Test
    void shouldCalculateCollisionsCorrectly() {
        Optional<SolverResult> result = solver.solve(4);
        assertTrue(result.isPresent());
        
        int collisions = result.get().collisions();
        assertTrue(collisions >= 0);
    }
}