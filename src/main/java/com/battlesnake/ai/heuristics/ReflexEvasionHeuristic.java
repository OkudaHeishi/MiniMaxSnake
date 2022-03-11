package com.battlesnake.ai.heuristics;

import com.battlesnake.ai.utils;
import com.battlesnake.internal.Direction;
import com.battlesnake.internal.Occupant;
import com.battlesnake.internal.SimWorld;

import java.awt.*;

public class ReflexEvasionHeuristic {

    public static float Score(SimWorld w, int ownIndex, Direction move) throws Exception {
        // If known to be deadly return minimum
        if (w.CertainlyDeadly(ownIndex, move)) return -1000.0f;

        Point newHead = utils.advance(move, w.snakes.get(ownIndex).head);

        float enemyCollisionScore = 0.0f;

        if (w.fields[newHead.y][newHead.x].occupant == Occupant.Snake) {
            enemyCollisionScore -= 1.0f;
        }

        float fruitScore = 0.0f;

        // Reward stepping on fruit
        if (w.fields[newHead.y][newHead.x].occupant == Occupant.Fruit) {
            fruitScore = 1.0f;
        }

        float potentialCollisionScore = 0.0f;

        // Punish proximity to enemy that may kill us
        for (int i = 0; i < w.snakes.size(); ++i) {
            if (!w.snakes.get(i).isAlive() || i == ownIndex) continue;

            int manhattan = utils.manhattanDistance(w.snakes.get(i).head, newHead);

            if (manhattan == 1) {
                // TODO: Delta does not take into account growing yet
                int delta = w.snakes.get(ownIndex).length - w.snakes.get(i).length;
                if (delta == 0) {
                    // Punish potential tie collision
                    potentialCollisionScore -= 0.5f;
                } else if (delta > 0) {
                    // Reward potential collision with smaller snake
                    // less than potential tie
                    potentialCollisionScore += 0.3f;
                } else {
                    // Punish potential collision with larger snake
                    potentialCollisionScore -= 1.0f;
                }
            }
        }
        float holdScore = move == w.snakes.get(ownIndex).getLastDirection() ? 1.0f : 0.0f;

        return holdScore + fruitScore * 3.0f + potentialCollisionScore * 10.0f + enemyCollisionScore * 30.0f;
    }
}

