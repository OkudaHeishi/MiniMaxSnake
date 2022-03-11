package com.battlesnake.internal;

import com.battlesnake.ai.utils;

import java.awt.*;
import java.io.Console;
import java.util.ArrayList;
import java.util.HashSet;

public class SimWorld {

    public Field[][] fields;

    public int turn;
    public int width;
    public int height;
    public ArrayList<Point> desiredPosition;
    public HashSet<Point> fruits;
    public ArrayList<SimSnake> snakes;

    private int TurnsSinceLastFoodSpawn;
    private int MaxTurnsToNextFoodSpawn;

    public SimWorld(int width, int height) throws Exception {
        if (width < 3 || height < 3) {
            throw new Exception("World size must be at least 3x3");
        }

        this.width = width;
        this.height = height;
        this.turn = 0;

        fields = new Field[height][width];

        snakes = new ArrayList<>();
        this.desiredPosition = new ArrayList<>();
        fruits = new HashSet<>();
        TurnsSinceLastFoodSpawn = 0;
        MaxTurnsToNextFoodSpawn = 20;
    }

    private SimWorld(SimWorld other) {
        width = other.width;
        height = other.height;
        turn = other.turn;
        TurnsSinceLastFoodSpawn = other.TurnsSinceLastFoodSpawn;
        MaxTurnsToNextFoodSpawn = other.MaxTurnsToNextFoodSpawn;

        fields = other.fields.clone();

        snakes = new ArrayList<>();
        desiredPosition = new ArrayList<>();
        fruits = new HashSet<>();

        for (SimSnake snake : other.snakes) {
            snakes.add(new SimSnake(snake, this));
        }

        fruits.addAll(other.fruits);
    }

    public static SimWorld FromApiModel(GameState state) throws Exception {
        Board board = state.board;
        SimWorld w = new SimWorld(board.width, board.height);
        w.turn = state.turn;

        for (Point f : board.food) {
            if (w.fields[f.y][f.x].occupant != Occupant.Empty) {
                continue;
            }

            w.fields[f.y][f.x] = new Field(Occupant.Fruit);
            boolean added = w.fruits.add(f);
        }

        for (int i = 0; i < board.snakes.size(); ++i) {
            Snake s = board.snakes.get(i);
            SimSnake simSnake = new SimSnake(w, i, s.getHead());

            if (w.fields[s.getHead().x][s.getHead().y].occupant != Occupant.Empty) {
                throw new Exception("Trying to place snake part on occupied tile");
            }

            Direction headDirection = Direction.NORTH;

            if (s.EffectiveLength() > 1) {
                headDirection = utils.GetSingleStepDirection(s.body.get(1), s.body.get(0));
            }

            w.fields[s.getHead().y][s.getHead().x] = new Field(Occupant.Snake, i, headDirection);

            // Add tails correctly
            for (int j = 1; j < s.EffectiveLength(); ++j) {
                if (w.fields[s.body.get(j).y][s.body.get(j).x].occupant != Occupant.Empty) {
                    throw new Exception("Trying to place snake part on occupied tile");
                }

                Direction directionLeft = utils.GetSingleStepDirection(s.body.get(j), s.body.get(j-1));
                w.fields[s.body.get(j).y][s.body.get(j).x] = new Field(Occupant.Snake, i, directionLeft);
            }

            // Set meta data
            simSnake.health = s.getHealth();
            simSnake.length = s.EffectiveLength();
            simSnake.maxLength = s.body.size();
            simSnake.pendingMaxLength = s.body.size();
            simSnake.tail = s.getTail();

            w.snakes.add(simSnake);
        }

        return w;
    }

    public int FindSnakeIndexForHead(Point pos) throws Exception {
        for (int i = 0; i < snakes.size(); ++i) {
            if (snakes.get(i).head == pos) {
                return i;
            }
        }

        throw new Exception("No such position");
    }

    public boolean CertainlyDeadly(int index, Direction d) throws Exception {
        SimSnake self = snakes.get(index);
        Point next = utils.advance(d, self.head);

        // Moving out of bounds is certainly gonna kill the snake
        if (next.x < 0 || next.y < 0 || next.x >= width || next.y >= height) return true;

        // Also running into a part of ourselves that is not our tail is certainly deadly
        Field field = fields[next.y][next.x];

        // Running into anything but a snake or running into any snake that isn't us
        // may always be safe, as other snakes may die
        if (field.occupant != Occupant.Snake || field.id != index) return false;

        // If tail pointer matches next position, running into ourself may be safe
        // (actually, should always be safe, because we can't grow as no fruit spawns on top of our tail)
        // Otherwise running into body, which will kill us
        return self.tail != next;
    }

    public void UpdateMovementTick(ArrayList<Direction> desiredDirections) throws Exception {
        if (desiredDirections.size() != snakes.size()) {
            throw new Exception("Dimensions must agree");
        }

        desiredPosition.clear();

        for (int i = 0; i < snakes.size(); ++i) {
            SimSnake s = snakes.get(i);

            if (s.isAlive()) {
                desiredPosition.add(utils.advance(desiredDirections.get(i), s.head));
            } else {
                // Add invalid position
                desiredPosition.add(new Point(-1, -1));
            }
        }

        // Kill on out of bounds
        for (int i = 0; i < snakes.size(); ++i) {
            if (!snakes.get(i).isAlive()) {
                continue;
            }

            Point pos = desiredPosition.get(i);

            if (pos.x < 0 || pos.y < 0 || pos.x >= width || pos.y >= height) {
                snakes.get(i).Kill(Status.KilledWall);
            }
        }

        // Check head on head collisions first, do this first to ensure
        // that fruits will always be consumed by the snake deserving to do so
        // Now handles three way collision
        for (int i = 0; i < snakes.size(); ++i) {

            if (!snakes.get(i).isAlive()) {
                continue;
            }

            Point target = desiredPosition.get(i);
            int matches = 1;

            // What's the longest tail
            int longest = snakes.get(i).peekLength();

            // Count how many snakes have lengths matching longest
            int longestCount = 1;

            // Preflight to determine longest snake and collisions
            // for given target
            for (int j = i + 1; j < snakes.size(); ++j) {
                if (!snakes.get(j).isAlive()) {
                    continue;
                }

                if (desiredPosition.get(j) == target) {
                    ++matches;

                    // If longest excelled, reset longest counter
                    if (longest < snakes.get(j).peekLength()) {
                        longest = snakes.get(j).peekLength();
                        longestCount = 1;
                    }
                    // Else if longest matched, increase longest counter
                    else if (longest == snakes.get(j).peekLength()) {
                        longestCount++;
                    }
                }
            }

            // If no collision, skip
            if (matches == 1) continue;

            // Kill all that would be killed by multi-way collision
            for (int j = i; j < snakes.size(); ++j) {
                if (!snakes.get(j).isAlive()) {
                    continue;
                }

                // Kill if shorter than longest or if more than 1 longest
                if (desiredPosition.get(j) == target && (snakes.get(j).peekLength() < longest || longestCount > 1)) {
                    snakes.get(j).Kill(Status.KilledHeadOnHead);
                }
            }
        }

        // Now consume fruits, do this before checking tail collision to ensure
        // that we cannot clip into a growing snake
        for (int i = 0; i < snakes.size(); ++i) {
            if (!snakes.get(i).isAlive()) {
                continue;
            }

            Point newHead = desiredPosition.get(i);

            if (fields[newHead.y][newHead.x].occupant == Occupant.Fruit) {
                fields[newHead.y][newHead.x] = new Field(Occupant.Empty);
                snakes.get(i).grow();
            }
        }

        // Check head on tail collision, must also take into account growing
        for (int i = 0; i < snakes.size(); ++i) {
            if (!snakes.get(i).isAlive()) {
                continue;
            }

            Point newHead = desiredPosition.get(i);
            Field f = fields[newHead.y][newHead.x];

            if (f.occupant == Occupant.Wall) {
                snakes.get(i).Kill(Status.KilledWall);
            } else if (f.occupant == Occupant.Snake) {
                // If colliding into the end of another snake, need to check
                // whether the other snake will grow, in which case this kills
                // the current snake, or if the other snake will not grow, freeing
                // the tile simultaneously
                if (!snakes.get(f.id).tail.equals(newHead) || snakes.get(f.id).willGrowOnUpdate()) {
                    if (f.id == i) {
                        snakes.get(i).Kill(Status.KilledOwnBody);
                    } else {
                        snakes.get(i).Kill(Status.KilledEnemyBody);
                    }
                }
            }
        }

        // Kill all starved snakes
        for (int i = 0; i < snakes.size(); ++i) {
            if (!snakes.get(i).isAlive()) {
                continue;
            }

            if (--snakes.get(i).health <= 0) {
                snakes.get(i).Kill(Status.KilledStarvation);
            }
        }

        // Now actually move all snakes, first move tails out of the way so that heads
        // may simulataneously enter old tails tile without comprising information
        for (int i = 0; i < snakes.size(); ++i) {
            if (snakes.get(i).isAlive()) {
                snakes.get(i).performTailMove();
            }
        }
        for (int i = 0; i < snakes.size(); ++i) {
            if (snakes.get(i).isAlive()) {
                snakes.get(i).performHeadMove(desiredDirections.get(i));
            }
        }

        // Update post tick statistics (transfer new max lenght)
        for (int i = 0; i < snakes.size(); ++i) {
            snakes.get(i).updatePostTick();
        }

        ++turn;
    }



}
