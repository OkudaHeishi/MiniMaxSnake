package com.battlesnake.internal;

public class Field {
    public Occupant occupant;
    public String id;
    public Direction direction;

    public Field(Occupant o, String id, Direction d) {
        occupant = o;
        this.id = id;
        direction = d;
    }

    public Field(Occupant o) {
        occupant = o;
        id = null;
        direction = Direction.NORTH;
    }

    public Field WithModifiedDirection(Direction d) {
        return new Field(occupant, id, d);
    }

}
