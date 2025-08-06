package org.worker.core;

import java.util.Optional;

public interface Solver {
    Optional<SolverResult> solve(int boardSize);
}
