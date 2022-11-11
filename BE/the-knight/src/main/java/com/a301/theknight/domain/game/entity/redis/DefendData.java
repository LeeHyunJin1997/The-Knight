package com.a301.theknight.domain.game.entity.redis;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;


@Data
public class DefendData implements Serializable {
    private final Hand defendHand;
    private final int shieldCount;
    private boolean isDefendPass;

    @Builder
    public DefendData(Hand hand, int shieldCount){
        this.defendHand = hand;
        this.shieldCount = shieldCount;
        this.isDefendPass = false;
    }

    public void defendPass(){
        this.isDefendPass = true;
    }
}
