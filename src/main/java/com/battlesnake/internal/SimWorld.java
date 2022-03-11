package com.battlesnake.internal;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;

public class SimWorld {

    public Field[][] fields;

    public int turn;
    public int width;
    public int height;
    public ArrayList<Point> desiredPosition;
    public HashSet<Point> fruits;
}
