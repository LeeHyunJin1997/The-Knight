package com.a301.theknight.domain.game.entity.redis;

import com.a301.theknight.domain.game.dto.playing.response.GameOrderDto;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder
public class TeamInfoData implements Serializable {
    private int currentAttackIndex;
    private int peopleNum;
    private GameOrderDto[] orderList;
    private long leaderId;
    private boolean selected;

    public void completeSelect() {
        selected = true;
    }
}