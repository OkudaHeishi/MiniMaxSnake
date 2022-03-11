package com.battlesnake.ai;

import com.battlesnake.internal.Direction;
import com.battlesnake.internal.SimWorld;
import com.sun.tools.javac.util.Pair;

import java.util.ArrayList;


public class AlphaBeta {
    public static Pair<Direction, Float> BestWithHeuristic(DeepeningSearch.Configuration c, SimWorld w, int maxDepth, int currentDepth, float alpha, float betaInitial) throws Exception {

        // Fail Sim if terminal or depth exhausted
        boolean terminal = c.Heuristic.IsTerminal(w);
        boolean limitReached = currentDepth >= maxDepth;

        if (terminal || limitReached) {
            Float score = c.Heuristic.Score(w);
            return new Pair<>(Direction.NORTH, score);
        }

        Direction bestOwnDirection = Direction.NORTH;

        ArrayList<Direction> desiredMoves = new ArrayList<>(w.snakes.size());
        for (int i = 0; i < w.snakes.size(); ++i) {
            // Use the given decision functions for all snakes first
            desiredMoves.add(c.UntargetedDecisionFunction(w, i));
        }

        // Initially have not checked any own moves. We must always check at least one
        // available action to allow the heuristic to evaluate one leaf, even if we know
        // it leads to death. Otherwise would return the theoretical heuristic min value (-Inf)
        // not the practical lower bound as implemented
        boolean checkedOwnMove = false;

        for (int i = 0; i < utils.Directions.length; ++i) {

            // If this is the last available action and we have not evaluated any actions,
            // must evaluate.
            boolean mustEvaluateOwn = !checkedOwnMove && i == utils.Directions.length - 1;

            // Skip guaranteed deadly immediately
            if (!mustEvaluateOwn && w.CertainlyDeadly(c.OwnIndex, utils.Directions[i])) {
                continue;
            }

            checkedOwnMove = true;

            // Must reset beta to initial beta value of this node, otherwise using updated beta from different sub tree
            // Beta stores the best move possible for the opponent
            float beta = betaInitial;

            boolean checkedEnemyMove = true;

            for (int j = 0; j < utils.Directions.length; ++j) {

                // Check stop request in inner loop
                if (c.stop.StopRequested()) throw new Exception();

                boolean mustEvaluateEnemy = !checkedEnemyMove && j == utils.Directions.length - 1;

                // Skip guaranteed deadly immediately
                if (!mustEvaluateEnemy && w.CertainlyDeadly(c.EnemyIndex, utils.Directions[j])) {
                    continue;
                }

                desiredMoves.set(c.OwnIndex, utils.Directions[i]);
                desiredMoves.set(c.EnemyIndex, utils.Directions[j]);

                SimWorld worldInstance = (SimWorld) w.clone();
                worldInstance.UpdateMovementTick(desiredMoves);

                Pair<Direction, Float> pair = BestWithHeuristic(c, worldInstance, maxDepth, currentDepth + 1, alpha, beta);

                if (pair.snd < beta) {
                    beta = pair.snd;
                }

                if (alpha >= beta) {
                    // Alpha cut-off
                    break;
                }
            }

            if (beta > alpha) {
                alpha = beta;
                bestOwnDirection = utils.Directions[i];
            }

            // If our best move is even better than the current choice of the opponent
            // stop, no need to find even better moves
            // Of course have to compare to initial beta value here
            if (alpha >= betaInitial) {
                // Beta cut-off
                break;
            }
        }

        return new Pair<Direction, Float>(bestOwnDirection, alpha);
    }

}
