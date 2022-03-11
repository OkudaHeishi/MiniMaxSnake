package com.battlesnake.internal;

import com.fasterxml.jackson.databind.JsonNode;

public class GameState {
    public final int turn;
    public Board board;
    public Snake you;

    public GameState(JsonNode moveRequest) {
        this.turn = moveRequest.get("turn").asInt();
        this.board = new Board(moveRequest.get("board"));
        this.you = new Snake(moveRequest.get("you"));
    }

}
