package com.a301.theknight.domain.player.dto;

import lombok.Data;

@Data
public class PlayerExitResponse {
    private long exitPlayerId;

    public PlayerExitResponse(long exitPlayerId){
        this.exitPlayerId = exitPlayerId;
    }
}