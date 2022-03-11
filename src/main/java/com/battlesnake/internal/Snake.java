package com.battlesnake.internal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

public class Snake {
    private final String id;
    private final String name;
    private int health;
    public ArrayList<Point> body;

    public Snake(JsonNode snakeNode) {
        this.id = snakeNode.get("id").asText();
        this.name = snakeNode.get("name").asText();
        this.health = snakeNode.get("health").asInt();
        this.body = new ArrayList<>();

        ArrayNode points = (ArrayNode) snakeNode.get("body");

        for (JsonNode point : points) {
            body.add(new Point(point.get("x").asInt(), point.get("y").asInt()));
        }
    }

    public int EffectiveLength() {
        return body.size() - GrowthLeft();
    }

    public Point getTail() {
        return this.body.get(body.size() - 1);
    }

    public int GrowthLeft() {
        int result = 0;
        Point tail = getTail();

        for (int i = body.size() - 2; i >= 0; --i) {
            if (body.get(i) == tail) ++result;
            else break;
        }

        return result;

    }

    public Point getHead() {
        return this.body.get(0);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

}

