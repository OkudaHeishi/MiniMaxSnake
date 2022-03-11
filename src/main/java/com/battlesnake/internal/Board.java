package com.battlesnake.internal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

public class Board {
    public int width;
    public int height;
    public ArrayList<Point> food;
    public ArrayList<Snake> snakes;

    public Board(JsonNode board) {
        this.width = board.get("width").asInt();
        this.height = board.get("height").asInt();
        this.food = new ArrayList<>();

        ArrayNode foodNodes = (ArrayNode) board.get("food");

        for (JsonNode foodElem : foodNodes) {
            this.food.add(new Point(foodElem.get("x").asInt(), foodElem.get("y").asInt()));
        }

        ArrayNode snakeNodes = (ArrayNode) board.get("snakes");

        this.snakes = new ArrayList<>();

        for (JsonNode snakeNode: snakeNodes) {
            this.snakes.add(new Snake(snakeNode));
        }
    }

    public boolean isOnBoard(Point p) {
        return p.x >= 0 && p.y >= 0 && p.x < width && p.y < height;
    }
}
