package com.battlesnake.ai;

import com.battlesnake.internal.Direction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.awt.*;
import java.util.*;

public class utils {

    public static Point up(Point point) {
        return new Point(point.x, point.y+1);
    }
    public static Point down(Point point) {
        return new Point(point.x, point.y-1);
    }
    public static Point left(Point point) {
        return new Point(point.x-1, point.y);
    }
    public static Point right(Point point) {
        return new Point(point.x+1, point.y);
    }

    public static ArrayList<Point> adjacentTiles(Point point, int height, int width) {

        ArrayList<Point> tiles = new ArrayList<>();

        int x = point.x;
        int y = point.y;

        if (x == 0 && y == 0) {
            tiles.add(up(point));
            tiles.add(right(point));

        } else if (x == 0 && y == height - 1) {
            tiles.add(right(point));
            tiles.add(down(point));

        } else if (x == width - 1 && y == height - 1) {
            tiles.add(left(point));
            tiles.add(down(point));

        } else if (x == width - 1 && y == 0) {
            tiles.add(up(point));
            tiles.add(left(point));

        } else if (x == 0) {
            tiles.add(up(point));
            tiles.add(down(point));
            tiles.add(right(point));

        } else if (y == 0) {
            tiles.add(up(point));
            tiles.add(left(point));
            tiles.add(right(point));

        } else if (y == height - 1) {
            tiles.add(down(point));
            tiles.add(left(point));
            tiles.add(right(point));

        } else if (x == width - 1) {
            tiles.add(left(point));
            tiles.add(down(point));
            tiles.add(up(point));
        } else {
            tiles.add(up(point));
            tiles.add(down(point));
            tiles.add(left(point));
            tiles.add(right(point));
        }

        return tiles;
    }

    public static ArrayList<Point> safeAdjacentTiles(ArrayList<Point> tiles, ArrayList<Point> ourSnake, ArrayList<Point> theirSnake){
        ArrayList<Point> safeTiles = new ArrayList<>();

        Point ourTail = ourSnake.get(ourSnake.size()-1);
        Point theirTail = theirSnake.get(theirSnake.size()-1);

        if (tiles.contains(ourTail)){
            safeTiles.add(ourTail);
        }
        if (tiles.contains(theirTail)) {
            safeTiles.add(theirTail);
        }

        for(Point tile : tiles) {
            if (!ourSnake.contains(tile) && !theirSnake.contains(tile)) safeTiles.add(tile);
        }

        return safeTiles;
    }

    public static int[][] boardTo2DArray(JsonNode board) {
        int[][] grid = new int[board.get("height").asInt()][board.get("width").asInt()];

        ArrayNode snakes = (ArrayNode) board.get("snakes");

        while(snakes.iterator().hasNext()) {
            ArrayNode points = (ArrayNode) snakes.iterator().next().get("body");

            while(points.iterator().hasNext()) {
                JsonNode point =  points.iterator().next();
                grid[point.get("x").asInt()][point.get("y").asInt()] = 1;
            }
        }

        return grid;

    }

    public static int manhattanDistance(Point source, Point destination) {
        return Math.abs(source.x - destination.x) + Math.abs(source.y - destination.y);
    }

    public static Point getAdjacentCoordinate(Point source, String directionString) {

        if (Objects.equals(directionString, "up")) {
            return up(source);
        } else if (Objects.equals(directionString, "left")) {
            return left(source);
        } else if (Objects.equals(directionString, "right")) {
            return right(source);
        } else {
            return down(source);
        }
    };

    public static boolean pointOutOfBounds (Point point, int height, int width) {
        if (point.x < 0 || point.y < 0 || point.x >= width || point.y >= height) {
            return true;
        } else {
            return false;
        }
    };

    public static Point advance(Direction d, Point source) throws Exception {
        switch (d) {
            case NORTH:
                return new Point(source.x, source.y - 1);
            case EAST:
                return new Point(source.x + 1, source.y);
            case SOUTH:
                return new Point(source.x, source.y + 1);
            case WEST:
                return new Point(source.x - 1, source.y);
            default:
                throw new Exception("Invalid value for Direction");
        }
    }

    public static Direction GetSingleStepDirection(Point from, Point to) throws Exception {
        if (manhattanDistance(from, to) != 1) {
            throw new Exception(String.format("Not a single step from {0} to {1}", from, to));
        }

        if (from.x < to.x) {
            return Direction.EAST;
        } else if (from.x > to.x) {
            return Direction.WEST;
        } else if (from.y < to.y) {
            return Direction.SOUTH;
        } else {
            return Direction.NORTH;
        }
    }




}
