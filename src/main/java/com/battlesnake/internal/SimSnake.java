package com.battlesnake.internal;

import com.battlesnake.ai.utils;

import java.awt.*;

public class SimSnake {

    public Point tail;
    public int index;


    public Status status;

    public int maxLength;

    public int pendingMaxLength;
    private SimWorld world;
    public Point head;
    public int health;
    public int length;



    public boolean isAlive() {
        return status == Status.Alive;
    }

    public Direction getLastDirection() {
        return world.fields[head.y][head.x].direction;
    }

    public SimSnake(SimWorld world, int index, Point initialHeadPosition) {
        this.world = world;
        this.index = index;
        maxLength = 3;
        pendingMaxLength = maxLength;
        length = 1;
        status = Status.Alive;
        health = 100;
        head = initialHeadPosition;
        tail = initialHeadPosition;
    }

    public SimSnake(SimSnake other, SimWorld world) {
        this.tail = other.tail;
        this.index = other.index;
        this.status = other.status;
        this.maxLength = other.maxLength;
        this.pendingMaxLength = other.pendingMaxLength;
        this.world = world;
        this.head = other.head;
        this.health = other.health;
        this.length = other.length;
    }

    public void Kill(Status killReason) throws Exception {
        if (killReason == Status.Alive) throw new Exception();

        // Clean up from tail up
        Point current = tail;

        for (;;) {
            Direction d = world.fields[current.y][current.x].direction;

            world.fields[current.y][current.x] = new Field(Occupant.Empty);

            if (current.equals(head)) {
                break;
            }

            current = utils.advance(d, current);
        }

        status = killReason;
    }

    public boolean willGrowOnUpdate() {
        return length != maxLength;
    }

    public int peekLength() {
        if (length == maxLength) {
            return length;
        } else {
            return length + 1;
        }
    }

    public void grow() {
        pendingMaxLength += 1;
        health = 101;
    }

    public void performTailMove() throws Exception {
        if (length == maxLength) {

            Point newTail = utils.advance(world.fields[tail.y][tail.x].direction, tail);

            world.fields[tail.y][tail.x] =new Field(Occupant.Empty);

            tail = newTail;

            --length;
        }
    }

    public void performHeadMove(Direction d) throws Exception {

        world.fields[head.y][head.x] = world.fields[head.y][head.x].WithModifiedDirection(d);

        head = utils.advance(d, head);

        world.fields[head.y][head.x] = new Field(Occupant.Snake, index, d);

        ++length;
    }

    public void updatePostTick() {
        maxLength = pendingMaxLength;
    }
}
