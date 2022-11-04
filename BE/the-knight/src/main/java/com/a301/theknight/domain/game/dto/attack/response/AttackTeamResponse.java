package com.a301.theknight.domain.game.dto.attack.response;

import com.a301.theknight.domain.game.dto.attack.AttackPlayerDto;
import com.a301.theknight.domain.game.dto.attack.DefendPlayerDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AttackTeamResponse {
    private AttackPlayerDto attacker;
    private DefendPlayerDto defender;
    private String weapon;
    private String hand;
    private String team;
}