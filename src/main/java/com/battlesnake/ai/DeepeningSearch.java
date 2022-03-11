package com.battlesnake.ai;

import com.battlesnake.ai.heuristics.IHeuristic;
import com.battlesnake.ai.heuristics.ReflexEvasionHeuristic;
import com.battlesnake.internal.Direction;
import com.battlesnake.internal.SimWorld;
import com.sun.tools.javac.util.Pair;

import java.util.Timer;
import java.util.TimerTask;

public abstract class DeepeningSearch {

    class Stop {
        private volatile boolean stop = false;

        public boolean StopRequested() {
            return stop;
        }

        public void RequestStop() {
            stop = true;
        }
    }

    public enum LimitType {
        Milliseconds,
        Depth
    }

    public class SearchLimit {

        public LimitType LimitType;
        public int Limit;


    }

    public class Configuration {
        public int OwnIndex;
        public int EnemyIndex;
        public IHeuristic<SimWorld> Heuristic;
        public Stop stop;
        public SearchLimit SearchLimit;

        public Direction UntargetedDecisionFunction(SimWorld sw, Integer i) throws Exception {
            return utils.ImprovedReflexBasedEvade(sw, i);
        }

        public Configuration(IHeuristic<SimWorld> heuristic, int ownIndex, int enemyIndex) {
            Heuristic = heuristic;
            OwnIndex = ownIndex;
            EnemyIndex = enemyIndex;
            SearchLimit = new SearchLimit();
        }
    }

    public class Result {
        public float Score;
        public Direction Move;
        public int Depth;

        public Result(float score, Direction move, int depth) {
            Score = score;
            Move = move;
            Depth = depth;
        }
    }
    public abstract Pair<Direction, Float> Search(Configuration c, SimWorld root, int depth);

    public Result Best(Configuration c, SimWorld w) throws Exception {
        switch (c.SearchLimit.LimitType) {
            case Depth:
                return BestFixedDepth(c, w);
            case Milliseconds:
                return BestFixedTime(c, w);
            default:
                throw new Exception();
        }
    }

    private Result BestFixedDepth(Configuration c, SimWorld w) {

        Pair<Direction, Float> pair = Search(c, w, c.SearchLimit.Limit);

        return new Result(pair.snd, pair.fst, c.SearchLimit.Limit);
    }

    private Result BestFixedTime(Configuration c, SimWorld w) {
        //TO-DO implement threads

        c.stop = new Stop();

        Object myLock = new Object();
        Result best = null;

        Pair<Direction, Float> current = Search(c, w, 1);

        return new Result(current.snd, current.fst, 1);

    }




}
