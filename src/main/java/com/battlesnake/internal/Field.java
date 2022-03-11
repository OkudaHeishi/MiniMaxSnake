package com.battlesnake.internal;

public class Field {
    public Occupant occupant;
    public int id;
    public Direction direction;

    public Field(Occupant o, int id, Direction d) {
        occupant = o;
        this.id = id;
        direction = d;
    }

    public Field(Occupant o) {
        occupant = o;
        id = 0;
        direction = Direction.NORTH;
    }

    public Field WithModifiedDirection(Direction d) {
        return new Field(occupant, id, d);
    }

}
