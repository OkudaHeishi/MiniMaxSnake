package com.battlesnake.ai;

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

        public LimitType LimitType()  LimitType.Milliseconds;

        public int Limit { get; set; } = 400;


    }

}
