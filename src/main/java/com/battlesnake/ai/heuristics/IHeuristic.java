package com.battlesnake.ai.heuristics;

public interface IHeuristic<S> {
    boolean IsTerminal(S state);
    float Score(S state);
}
