package com.battlesnake.internal;

public enum Direction {
    NORTH,
    WEST,
    SOUTH,
    EAST;

    public static Direction Complement(Direction d) throws Exception {
        if (d == Direction.NORTH) {
            return Direction.SOUTH;
        } else if (d == Direction.EAST){
            return Direction.WEST;
        } else if (d == Direction.SOUTH) {
            return Direction.NORTH;
        } else if(d == Direction.WEST) {
            return Direction.EAST;
        } else{
            throw new Exception("Invalid value for this Direction");
        }
    }

    public static Direction NextLeft(Direction d) throws Exception {
        if (d == Direction.NORTH) {
            return Direction.WEST;
        } else if (d == Direction.EAST){
            return Direction.NORTH;
        } else if (d == Direction.SOUTH) {
            return Direction.EAST;
        } else if(d == Direction.WEST) {
            return Direction.SOUTH;
        } else{
            throw new Exception("Invalid value for this Direction");
        }
    }

    public static Direction NextRight(Direction d) throws Exception {
        if (d == Direction.NORTH) {
            return Direction.EAST;
        } else if (d == Direction.EAST) {
            return Direction.SOUTH;
        } else if (d == Direction.SOUTH) {
            return Direction.WEST;
        } else if (d == Direction.WEST) {
            return Direction.NORTH;
        } else {
            throw new Exception("Invalid value for this Direction");
        }
    }

    public static boolean Subtended(Direction a, Direction b) throws Exception {
        return a == Complement(b);
    }

}
